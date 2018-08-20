package app.models;

import app.models.Contact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void getKey_invalidNameInput_returnsValidKey() {
        String expected = "natevojtik";
        Contact contact = new Contact("N'ate Vojtik", "6306151042", "test@gmail.com");
        assertEquals(expected, contact.getKey());
    }

    @Test
    void cleanName_inputWithPunctuation_returnsNameWithoutPunctuation() {
        String expected = "Nate Vojtik";
        String actual = Contact.cleanName("N'ate Vojtik");
        assertEquals(expected, actual);
    }

    @Test
    void cleanName_inputWithNumbers_returnsNameWithoutNumbers() {
        String expected = "Nate Vojtik";
        String actual = Contact.cleanName("Nat3e Vojtik");
        assertEquals(expected, actual);
    }

    @Test
    void cleanPhone_tooManyNumbers_returnsNull() {
        String number = Contact.cleanPhone("123456789011");
        assertNull(number);
    }

    @Test
    void cleanPhone_invalidFormat_returnsNull() {
        String number = Contact.cleanPhone("[123] 456-7890");
        assertNull(number);
    }

    @Test
    void cleanPhone_validFormat_returnsNumberUnchanged() {
        String expected = "(123) 456-7910";
        String actual = Contact.cleanPhone(expected);
        assertEquals(expected, actual);
    }

    @Test
    void cleanEmail_validEmail_returnsInputUnchanged() {
        String expected = "home@email.com";
        String actual = Contact.cleanEmail("home@email.com");
        assertEquals(expected, actual);
    }

    @Test
    void cleanEmail_invalidEmail_returnsNull() {
        String cleaned = Contact.cleanEmail("homeemail.com");
        assertNull(cleaned);
    }

    @Test
    void toJson_serializableFieldsNull_outputHasNulls() {
        Contact test = new Contact("Nate Vojtik", "6306151042", null);
        String expected = "{\"name\":\"Nate Vojtik\",\"phone\":\"6306151042\",\"email\":null}";
        assertEquals(expected, Contact.toJson(test));
    }

    @Test
    void fromJsonString_invalidName_outputHasValidName() {
        String test = "{\"name\":\"N'ate V2ojtik\",\"phone\":\"6306151042\",\"email\":\"npvojtik@gmail.com\"}";
        String expected = "Nate Vojtik";
        Contact contact = Contact.fromJsonString(test);
        assertEquals(expected, contact.getName());
    }

    @Test
    void copyFrom_inputWithNullFields_nullFieldsNotCopied() {
        Contact test = new Contact("Nate Vojtik", "6306151042", "not@real.com");
        Contact overwrite = new Contact(null, null, "npvojtik@gmail.com");
        test.copyFrom(overwrite);
        assertNotNull(test.getName());
    }
}