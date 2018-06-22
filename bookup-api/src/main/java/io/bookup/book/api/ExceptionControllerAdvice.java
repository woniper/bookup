package io.bookup.book.api;

import io.bookup.book.domain.NotFoundBookException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author woniper
 */
@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundBookException.class)
    public ResponseEntity<ErrorResponse> notFoundBook(NotFoundBookException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse
                        .builder()
                        .message(exception.getMessage())
                        .build());
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ErrorResponse {
        private String message;
    }
}
