package app.requesthandler;

import app.converter.ContactJsonConverter;
import app.dao.ContactDAOResponse;
import app.dao.DAOResponse;
import app.models.Contact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SparkContactRequestHandlerTest {

    private static final SparkContactRequestHandler handler = new SparkContactRequestHandler(null, new ContactJsonConverter());

    @Test
    void parseResponse_successfulResponse_returnsJSONOfPayload() {
        String expected = "{\"name\":\"Valid Name\",\"phone\":\"0123456789\",\"email\":\"email@mail.com\"}";
        DAOResponse<Contact> daoResponse = new ContactDAOResponse(new Contact("Valid Name",
                "0123456789", "email@mail.com"));
        assertEquals(handler.parseResponse(daoResponse), expected);
    }

    @Test
    void parseResponse_unsuccessfulResponseNoException_returnsResponseMessage() {
        String expected = "{Record not found}";
        DAOResponse<Contact> daoResponse = new ContactDAOResponse("Record not found", null);
        assertEquals(expected, handler.parseResponse(daoResponse));
    }

    @Test
    void parseResponse_unsuccessfulResponseWithException_returnsResponseAndExceptionMessages() {
        String expected = "{Cannot delete; Delete exception, record not found}";
        DAOResponse<Contact> daoResponse = new ContactDAOResponse("Cannot delete",
                new Exception("Delete exception, record not found"));
        assertEquals(expected, handler.parseResponse(daoResponse));
    }
}