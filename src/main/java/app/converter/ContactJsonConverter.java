package app.converter;

/**
 * Converts JSON strings to Contact objects and vice-versa using the static
 * conversion methods defined in the Contact model.
 */

import app.models.Contact;

public class ContactJsonConverter implements Converter<Contact, String> {

    public String to(Contact contact) {
        return Contact.toJson(contact);
    }

    public Contact from(String json) {
        return Contact.fromJsonString(json);
    }

}
