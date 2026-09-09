package com.example.springbd3big.book.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book not found with id=" + bookId);
    }
}
