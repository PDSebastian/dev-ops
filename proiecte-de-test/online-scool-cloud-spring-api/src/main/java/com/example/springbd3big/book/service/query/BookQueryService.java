package com.example.springbd3big.book.service.query;

import com.example.springbd3big.book.dtos.BookResponse;

import java.util.List;

public interface BookQueryService {
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long bookId);
    List<BookResponse> findByStudentId(Long studentId);
    List<BookResponse> searchBooks(String query);
}
