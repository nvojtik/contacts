package app.dao;

/**
 * Abstraction of a database / data store object
 */

import java.util.List;

public interface DAO<I, O extends DAOResponse<I>> {

    O getByKey(String key);

    List<O> getByQuery(String query, int pageSize, int page);

    O post(I toPost);

    O put(String key, I toUpdate);

    O delete(String key);

}
