package app.dao;

/**
 * Represents a response from a database access object; contains a @payload
 * (which may be null if the request is unsuccessful), an indicator of
 * the @success of the operation, a @message explaining what happened if
 * the operation failed, and an @exception if one was thrown by the operation.
 */

import app.models.Contact;

public class ContactDAOResponse implements DAOResponse<Contact> {

    private Contact payload;
    private boolean success;
    private String message;
    private Exception exception;

    public ContactDAOResponse(Contact payload, boolean success, String message, Exception exception) {
        this.payload = payload;
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public ContactDAOResponse(Contact payload) {
        this(payload, true, null, null);
    }

    public ContactDAOResponse(String message) { this(null, false, message, null); }

    public ContactDAOResponse(String message, Exception exception) {
        this(null, false, message, exception);
    }

    public Contact payload() {
        return payload;
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }

    public Exception exception() {
        return exception;
    }
}
