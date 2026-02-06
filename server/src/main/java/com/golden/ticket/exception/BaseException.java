package com.golden.ticket.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.io.Serializable;

@Getter
public abstract class BaseException extends RuntimeException implements Serializable {
    protected final ProblemDetail body;

    protected BaseException(String message) {
        super(message);
        this.body = ProblemDetail.forStatusAndDetail(this.getHttpStatusCode(), message);
    }

    public abstract HttpStatusCode getHttpStatusCode();
}