package app.dao;

import app.models.Contact;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DAOTest {

    private Contact contact1 = new Contact("Test A", "0001112222", "e@mail.com");
    private Contact contact2 = new Contact("Test B", null, "mail@example.com");
    private Contact contact3 = new Contact("null a", null, null);
    private Contact contact4 = new Contact("null b", null, null);

    // simple mock DAO for testing
    private DAO<Contact, DAOResponse<Contact>> dao = new DAO<Contact, DAOResponse<Contact>>() {
        Map <Contact, DAOResponse<Contact>> contacts = new HashMap<>();

        {
            contacts.put(contact1, new ContactDAOResponse(contact1));
            contacts.put(contact2, new ContactDAOResponse(contact2));
            contacts.put(contact3, new ContactDAOResponse(contact3, false, "Connection error", new IOException("just a test")));
            contacts.put(contact4, new ContactDAOResponse(contact4, false, "Record not found", null));
        }

        public DAOResponse<Contact> getByKey(String key) {
            DAOResponse<Contact> response = new ContactDAOResponse("record not found", new Exception());
            for (Contact contact : contacts.keySet()) {
                if (contact.getKey().equals(key)) {
                    response = contacts.get(contact);
                }
            }
            return response;
        }

        public List<DAOResponse<Contact>> getByQuery(String query, int pageSize, int page) {
            return new ArrayList<>(contacts.values());
        }

        public DAOResponse<Contact> post(Contact contact) {
            DAOResponse<Contact> response = new ContactDAOResponse("cannot post", new Exception());
            if (contact != null) {
                contacts.put(contact, new ContactDAOResponse(contact));
                response = contacts.get(contact);
            }
            return response;
        }

        public DAOResponse<Contact> put(String key, Contact contact) {
            DAOResponse<Contact> response = getByKey(key);
            if (response.success()) {
                contacts.remove(response.payload());
                contacts.put(contact, new ContactDAOResponse(contact));
                response = contacts.get(contact);
            } else {
                response = new ContactDAOResponse("cannot update", new Exception());
            }
            return response;
        }

        public DAOResponse<Contact> delete(String key) {
            DAOResponse<Contact> response = new ContactDAOResponse("cannot delete", new Exception());
            Contact keyContact = null;
            for (Contact current : contacts.keySet()) {
                if (current.getKey().equals(key)) {
                    keyContact = current;
                }
            }
            if (keyContact != null) {
                response = new ContactDAOResponse(keyContact);
                contacts.remove(keyContact);
            }
            return response;
        }
    };

    @Test
    void getByKey_validKey_returnsSuccessfulResponse() {
        DAOResponse<Contact> response = dao.getByKey("testa");
        assertTrue(response.success());
    }

    @Test
    void getByKey_invalidKey_returnsUnsuccessfulResponse() {
        DAOResponse<Contact> response = dao.getByKey("notreal");
        assertFalse(response.success());
    }

    @Test
    void post_validInput_returnsSuccessfulResponse() {
        DAOResponse<Contact> response = dao.post(new Contact("Nate Vojtik", "6306151042", "npvojtik@gmail.com"));
        assertTrue(response.success());
    }

    @Test
    void post_invalidInput_returnsUnsuccessfulResponse() {
        DAOResponse<Contact> response = dao.post(null);
        assertFalse(response.success());
    }

    @Test
    void put_validInput_returnsSuccessfulResponse() {
        DAOResponse<Contact> response = dao.put("testa", new Contact("Test A", "0001112222", "email@mail.com"));
        assertTrue(response.success());
    }

    @Test
    void put_invalidInput_returnsUnsuccessfulResponse() {
        DAOResponse<Contact> response = dao.put("notreal", null);
        assertFalse(response.success());
    }

    @Test
    void delete_validInput_returnsSuccessfulResponse() {
        DAOResponse<Contact> response = dao.delete("testa");
        assertTrue(response.success());
    }

    @Test
    void delete_invalidInput_returnsUnsuccessfulResponse() {
        DAOResponse<Contact> response = dao.delete("notreal");
        assertFalse(response.success());
    }
}