package com.example.springbd3big.book.exceptions;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String bookName) {
        super("Book already exists with name=" + bookName);
    }
}
