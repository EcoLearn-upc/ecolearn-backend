package com.upc.ecolearn.exception;

import org.springframework.http.HttpStatus;

public class EcoLearnException extends RuntimeException {

    private final HttpStatus status;

    public EcoLearnException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}