package com.golden.ticket.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleApiException(BaseException ex, ServletWebRequest request) {
        ex.getBody().setInstance(URI.create(request.getRequest().getRequestURI()));
        return ResponseEntity.status(ex.getHttpStatusCode()).body(ex.getBody());
    }
}
