package app.dao;

/**
 * Implementation of a database access object for Elasticsearch.
 */

import app.converter.Converter;
import app.models.Contact;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchDAO implements DAO<Contact, DAOResponse<Contact>> {

    private final RestHighLevelClient client;
    private final Converter<Contact, String> converter;

    public ElasticSearchDAO(String hostName, int portNum, String scheme, Converter<Contact, String> converter) {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(hostName, portNum, scheme)
                )
        );
        this.converter = converter;
    }

    /**
     * Constructor using Elasticsearch's default local configuration.
     * @param converter = an implementation of Converter<S, T> that transforms
     *        instances of the data model to JSON (used by Elasticsearch) and
     *        vice-versa.
     */
    public ElasticSearchDAO(Converter<Contact, String> converter) {
        this("localhost", 9200, "http", converter);
    }

    /**
     * Fetches a document from Elasticsearch wrapped in a DAOResponse.
     * @param key = the unique key under which the desired document is stored in
     *        ElasticSearch.
     * @return A DAOResponse which wraps a Contact object generated from the
     *        requested document; if the document does not exist or an error occurs,
     *        the DAOResponse conveys this information too.
     */
    public DAOResponse<Contact> getByKey(String key) {

        DAOResponse<Contact> daoResponse;
        GetRequest request = new GetRequest("contacts", "doc", key);

        try {

            GetResponse response = client.get(request);
            if (response.isExists()) {
                daoResponse = new ContactDAOResponse(converter.from(response.getSourceAsString()));
            } else {
                daoResponse = new ContactDAOResponse("Record not found");
            }

        } catch (ElasticsearchException e) {
            daoResponse = new ContactDAOResponse("Elasticsearch error: " + e.status(), e);
        } catch (IOException e) {
            daoResponse = new ContactDAOResponse("Connection error", e);
        }

        return daoResponse;
    }

    /**
     * Fetches a list of DAOResponses wrapping documents from Elasticsearch
     * based on a query string query passed as a string.
     * @param query = a query string query represented as a String.
     * @param pageSize = the number of results to return per query.
     * @param page = the results offset.
     * @return a List of DAOResponses wrapping Contact instances generated
     *        from Elasticsearch based on the query.
     */
    public List<DAOResponse<Contact>> getByQuery(String query, int pageSize, int page) {

        SearchRequest request = new SearchRequest("contacts");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.queryStringQuery(query));
        builder.from(page);
        builder.size(pageSize);
        request.source(builder);

        List<DAOResponse<Contact>> results = new ArrayList<>();
        try {

            SearchResponse response = client.search(request);
            SearchHits responseHits = response.getHits();
            SearchHit[] hits = responseHits.getHits();

            for (SearchHit hit : hits) {
                results.add(
                        new ContactDAOResponse(converter.from(hit.getSourceAsString()))
                );
            }

        } catch (ElasticsearchException e) {
            results.add(new ContactDAOResponse("Elasticsearch error: " + e.status(), e));
        } catch (IOException e) {
            results.add(new ContactDAOResponse("Connection error", e));
        }

        return results;
    }

    /**
     * Adds a document to Elasticsearch.
     * @param contact = the Contact to add to Elasticsearch.
     * @return a DAOResponse wrapping the added Contact if successful
     *         or an error message / exception in the case of failure.
     */
    public DAOResponse<Contact> post(Contact contact) {
        DAOResponse<Contact> daoResponse;
        IndexRequest request = new IndexRequest("contacts", "doc", contact.getKey());
        request.source(converter.to(contact), XContentType.JSON);
        request.opType("create"); // enforce unique id

        try {
            IndexResponse result = client.index(request);
            daoResponse = new ContactDAOResponse(contact);
        } catch (ElasticsearchException e) {
            daoResponse = new ContactDAOResponse("Elasticseach error: " + e.status(), e);
        } catch (IOException e) {
            daoResponse = new ContactDAOResponse("Connection error", e);
        }
        return daoResponse;
    }

    /**
     * Updates a document in Elasticsearch.
     * @param key = the unique key under which the desired document is stored in
     *        ElasticSearch.
     * @param contact = the updated Contact information (some fields may be null
     *        if they are not updated).
     * @return a DAOResponse wrapping a Contact object representing the updated
     *        document if successful, or an error message and exception if the
     *        operation fails.
     */
    public DAOResponse<Contact> put(String key, Contact contact) {

        // unique id will change if name is updated, so new entry must be created
        DAOResponse<Contact> deleteResponse = delete(key);

        if (deleteResponse.success()) {
            contact = deleteResponse.payload().copyFrom(contact);
        } else {
            return deleteResponse;
        }

        return post(contact);
    }

    /**
     * Removes a document in Elasticsearch
     * @param key = the unique key under which the desired document is stored in
     *        ElasticSearch.
     * @return a DAOResponse wrapping a Contact object representing the deleted
     *        document if successful, or an error message and exception if the
     *        operation fails.
     */
    public DAOResponse<Contact> delete(String key){

        DAOResponse<Contact> daoResponse;
        DAOResponse<Contact> getResponse = getByKey(key);

        if (getResponse.success()) {

            try {

                DeleteRequest request = new DeleteRequest("contacts", "doc", key);
                DeleteResponse deleteResponse = client.delete(request);
                daoResponse = new ContactDAOResponse(getResponse.payload());

            } catch (ElasticsearchException e) {
                daoResponse = new ContactDAOResponse("Elasticsearch error: " + e.status(), e);
            } catch (IOException e) {
                daoResponse = new ContactDAOResponse("Connection error", e);
            }

        } else { // document to be deleted doesn't exist, just pass old resposne
            daoResponse = getResponse;
        }

        return daoResponse;
    }

}
