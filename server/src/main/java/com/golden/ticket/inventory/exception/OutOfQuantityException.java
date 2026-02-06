package com.golden.ticket.inventory.exception;

import com.golden.ticket.exception.BaseException;
import org.springframework.http.HttpStatusCode;

public class OutOfQuantityException extends BaseException {
    public OutOfQuantityException(String message) {
        super(message);
    }

    @Override
    public HttpStatusCode getHttpStatusCode() {
        return HttpStatusCode.valueOf(409);
    }
}
