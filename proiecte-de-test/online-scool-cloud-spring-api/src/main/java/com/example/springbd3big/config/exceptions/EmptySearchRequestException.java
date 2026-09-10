package com.example.springbd3big.config.exceptions;

public class EmptySearchRequestException extends RuntimeException {
    public EmptySearchRequestException() {
        super("Search query must not be empty");
    }
}
