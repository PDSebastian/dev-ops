package com.example.springbd3big.config.exceptions;

public class EmptyUpdateRequestException extends RuntimeException {
    public EmptyUpdateRequestException() {
        super("Update request must contain at least one field to update");
    }
}
