package com.example.springbd3big.config.exceptions;

public class RequestBodyMissingException extends RuntimeException {
    public RequestBodyMissingException() {
        super("Request body is missing");
    }
}
