package com.example.springbd3big.user.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("user with id " + userId + " not found");
    }
}
