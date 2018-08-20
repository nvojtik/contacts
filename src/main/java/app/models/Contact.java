package app.models;

/**
 * Data model for a contact in the contact book.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class Contact {

    // more fields (address, multiple phone numbers, splitting the name
    // into two fields, etc) can be added with ease, but these are
    // sufficient to show the functioning of the API
    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    public Contact(Contact other) {
        this.copyFrom(other);
    }

    public Contact(String name, String phone, String email) {

        this.name = cleanName(name);
        this.phone = cleanPhone(phone);
        this.email = cleanEmail(email);
    }

    public String getName() {
        return name;
    }

    /**
     * If @name is not null, removes invalid characters (anything that isn't
     * a letter or a space) and returns the cleaned String; otherwise, returns
     * null.
     */
    public static String cleanName(String name) {
        if (name != null) {
            return name.replaceAll("[^a-zA-z\\s]", "");
        } else {
            return null;
        }
    }

    public void setName(String name) {
        this.name = cleanName(name);
    }

    public String getPhone() {
        return phone;
    }

    /**
     * If @phone is not null, removes any invalid characters (anything that
     * isn't a digit, a parenthesis, or a hyphen, and checks that the phone
     * number fits one of four allowable patterns, including:
     *      0123456789
     *      (012)3456789
     *      (012)345-6789
     *      (012) 345-6789
     * If it fits, returns the clean phone number; otherwise, returns null.
     */
    public static String cleanPhone(String phone) {
        String output = null;
        if (phone != null) {
            phone.replaceAll("/[^0-9()-]/", "");

            // allows:
            // 0123456789
            // (012)3456789
            // (012)345-6789
            // (012) 345-6789
            String pattern = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\s?\\d{3}-?\\d{4}";
            if (phone.matches(pattern)) {
                output = phone;
            }
        }
        return output;
    }

    public void setPhone(String phone) {
        this.phone = cleanPhone(phone);
    }

    public String getEmail() {
        return email;
    }

    /**
     * Simply checks if an email address follows the basic format; will
     * not catch more complex invalid emails.
     */
    public static String cleanEmail(String email) {
        String pattern = "^(.+)@(.+)$";
        if (email != null && email.matches(pattern)) {
            return email;
        } else {
            return null;
        }
    }

    public void setEmail(String email) {
        this.email = cleanEmail(email);
    }

    /**
     * Generates a unique key from the @name by converting it to lower case
     * and removing all characters that are not lowercase letters. Note that
     * this also removes unicode characters such as é, which would require
     * more complex regex expressions to deal with. The key is how the document
     * is fetched by the REST client (i.e. a contact with the name Josh Miller
     * would be reached via GET /contact/joshmiller, but in this simple
     * implementation a contact with the name José Miller would be stored with
     * the key josmiller.
     */
    public String getKey() {
        return this.name.toLowerCase().replaceAll("[^a-z]", "");
    }

    public static String toJson(Contact contact) {
        return gson.toJson(contact);
    }

    public static Contact fromJsonString(String json) {
        return new Contact(gson.fromJson(json, Contact.class));
    }

    /**
     * Copies the values from one Contact object to another, provided they are not
     * null; mainly used for partial document updates.
     */
    public Contact copyFrom(Contact other) {
        if (other.name != null) {
            this.name = cleanName(other.name);
        }

        if (other.phone != null) {
            this.phone = cleanPhone(other.phone);
        }

        if (other.email != null) {
            this.email = cleanEmail(other.email);
        }

        return this;
    }
}
