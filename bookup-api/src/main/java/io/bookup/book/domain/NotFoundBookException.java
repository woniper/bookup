package io.bookup.book.domain;

/**
 * @author woniper
 */
public class NotFoundBookException extends RuntimeException {

    public NotFoundBookException(String message) {
        super(message);
    }
}
