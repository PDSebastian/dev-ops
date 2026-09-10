package com.example.springbd3big.user.administrator.exceptions;

public class AdministratorNotFoundException extends RuntimeException {

    public AdministratorNotFoundException(Long administratorId) {
        super("administrator with id " + administratorId + " not found");
    }

    public AdministratorNotFoundException(String email) {
        super("administrator with email " + email + " not found");
    }
}
