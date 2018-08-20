package app.dao;

/**
 * Abstraction of a response from a database / data store object.
 * @param <T> = the type corresponding to the data model.
 */

public interface DAOResponse<T> {

    T payload();

    boolean success();

    String message();

    Exception exception();

}
