package com.example.springbd3big.book.service.command;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;

public interface BookCommandService {
    BookResponse createBook(BookCreateRequest req);
    BookResponse updateBook(Long bookId, BookUpdateRequest req);
    BookResponse deleteBook(Long bookId);
}
