package app;

import app.converter.ContactJsonConverter;
import app.converter.Converter;
import app.dao.DAO;
import app.dao.DAOResponse;
import app.dao.ElasticSearchDAO;
import app.models.Contact;
import app.requesthandler.RequestHandler;
import app.requesthandler.SparkContactRequestHandler;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        Converter<Contact, String> converter = new ContactJsonConverter();
        DAO<Contact, DAOResponse<Contact>> dao = new ElasticSearchDAO(converter);
        RequestHandler<String, String> handler = new SparkContactRequestHandler(dao, converter);

        get("/contact/:name", (req, res) -> {
            String key = req.params(":name");
            return handler.get(key);
        });

        get("/contact", (req, res) -> {
            String query = req.queryParams("query");
            int pageSize = Integer.parseInt(req.queryParams("pageSize"));
            int page = Integer.parseInt(req.queryParams("page"));
            return handler.get(query, pageSize, page);
        });

        post("/contact", (req, res) -> {
            String json = req.body();
            return handler.post(json);
        });

        put("/contact/:name", (req, res) -> {
            String key = req.params(":name");
            String json = req.body();
            return handler.put(key, json);
        });

        delete("/contact/:name", (req, res) -> {
            String key = req.params(":name");
            return handler.delete(key);
        });
    }
}
