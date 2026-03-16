package com.bookstore.rest.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Standard error response body returned by the GlobalExceptionMapper.
 */
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;
    private String message;
    private Date timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
