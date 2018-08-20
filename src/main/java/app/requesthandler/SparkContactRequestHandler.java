package app.requesthandler;

/**
 * Handles the passing of requests made to the Spark-based REST user interface
 * to the database access object.
 */

import app.converter.Converter;
import app.dao.DAO;
import app.dao.DAOResponse;
import app.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class SparkContactRequestHandler implements RequestHandler<String, String> {

    private final DAO<Contact, DAOResponse<Contact>> dao;
    private final Converter<Contact, String> converter;

    public SparkContactRequestHandler(DAO<Contact, DAOResponse<Contact>> dao, Converter<Contact, String> converter) {
        this.dao = dao;
        this.converter = converter;
    }

    /**
     * Fetches a contact based on a unique @key.
     * @param key = the unique key based on the contact's name.
     * @return a JSON representation of the Contact if successful, or an error
     *        statement if not.
     */
    public String get(String key) {
        DAOResponse<Contact> response = dao.getByKey(key);
        return parseResponse(response);
    }

    /**
     * Fetches a lsit of contacts based on a @query.
     * @param query = a String query to be passed to the database.
     * @param pageSize = the number of results to return per query.
     * @param page = the results offset.
     * @return a List of JSON representations of the Contact objects representing
     *        the results of a query (if there are results) or a List containing
     *        a JSON representation of an error if one occurred.
     */
    public List<String> get(String query, int pageSize, int page) {
        List<String> results = new ArrayList<>();
        page = page * pageSize;
        List<DAOResponse<Contact>> responseList = dao.getByQuery(query, pageSize, page);
        if (responseList.size() == 0) { // no results, just return the empty lsit
            return results;
        }
        if (!responseList.get(0).success()) {
            results.add(responseList.get(0).message() + responseList.get(0).exception().getMessage());
        } else {
            for (DAOResponse<Contact> response : responseList) {
                results.add(converter.to(response.payload()));
            }
        }
        return results;
    }

    /**
     * Adds a contact to the database based on a JSON representation passed
     * in the body of the POST request.
     * @param toPost = the JSON representation of the contact information.
     * @return a JSON representation of the added contact if the operation was
     *        successful, or an error message otherwise.
     */
    public String post(String toPost) {
        Contact contact = converter.from(toPost);
        DAOResponse<Contact> response = dao.post(contact);
        return parseResponse(response);
    }

    /**
     * Updates a contact in the database based on the unique @key and a JSON
     * representation passed in the body of the PUT request.
     * @param key = the unique key based on the contact's name.
     * @param toUpdate = the JSON representation of the updated contact
     *       information (may be a partial update).
     * @return a JSON representation of the updated contact if the operation was
     *        successful, or an error message otherwise.
     */
    public String put(String key, String toUpdate) {
        Contact contact = converter.from(toUpdate);
        DAOResponse<Contact> response = dao.put(key, contact);
        return parseResponse(response);
    }

    /**
     * Deletes a contact in the database based on the unique @key.
     * @param key = the unique key based on the contact's name.
     * @return a JSON representation of the deleted contact information if successful,
     *        or an error message otherwise.
     */
    public String delete(String key) {
        DAOResponse<Contact> response = dao.delete(key);
        return parseResponse(response);
    }

    /**
     * Parses the database access object response into a JSON representation to be
     * returned by the API.
     * @param response = a DAOResponse wrapping a Contact object representing the
     *       contact information (or an error message / exception if something
     *       went wrong).
     * @return a JSON representation of the Contact information wrapped by the
     *       DAOResponse.
     */
    public String parseResponse(DAOResponse<Contact> response) {
        String output;
        if (response.success()) {
            output = converter.to(response.payload());
        } else if (response.exception() == null) {
            output = "{" + response.message() + "}";
        } else {
            output = "{" + response.message() + "; " + response.exception().getMessage() + "}";
        }
        return output;
    }

}
