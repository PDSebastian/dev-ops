package com.example.springbd3big.book.mappers;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookCreateRequest req) {
        if (req == null) return null;

        return Book.builder()
                .bookName(req.bookName())
                .build();
    }

    public BookResponse toDto(Book book) {
        Integer studentId = book.getStudent() != null ? book.getStudent().getId() : null;
        return new BookResponse(
                book.getId(),
                book.getBookName(),
                studentId
        );
    }
}
