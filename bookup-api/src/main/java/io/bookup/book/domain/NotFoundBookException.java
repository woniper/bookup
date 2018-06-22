package io.bookup.book.domain;

/**
 * @author woniper
 */
public class NotFoundBookException extends RuntimeException {

    public NotFoundBookException(String message) {
        super(String.format("찾을 수 없는 도서 정보입니다. (%s)", message));
    }
}
