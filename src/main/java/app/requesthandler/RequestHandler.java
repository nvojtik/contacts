package app.requesthandler;

/**
 * Abstraction of the communication layer between the user interface
 * and the database access object.
 */

import java.util.List;

public interface RequestHandler<I, O> {

    O get(String key);

    List<O> get(String query, int pageSize, int page);

    O post(I toPost);

    O put(String key, I toUpdate);

    O delete(String key);

}
