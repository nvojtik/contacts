package app.converter;

/**
 * Converts one data type to another.
 */

public interface Converter<S, T> {

    T to(S s);

    S from(T t);

}
