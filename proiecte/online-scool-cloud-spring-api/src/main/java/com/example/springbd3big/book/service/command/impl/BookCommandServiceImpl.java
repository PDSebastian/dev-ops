package com.example.springbd3big.book.service.command.impl;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import com.example.springbd3big.book.exceptions.BookNotFoundException;
import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.book.mappers.BookMapper;
import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.book.service.command.BookCommandService;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BookCommandServiceImpl implements BookCommandService {

    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final BookMapper bookMapper;

    public BookCommandServiceImpl(BookRepository bookRepository, StudentRepository studentRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest req) {
        if (req == null) {
            throw new RequestBodyMissingException();
        }

        Book book = bookMapper.toEntity(req);
        if (req.studentId() != null) {
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new StudentNotFoundException(req.studentId()));
            book.setStudent(student);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long bookId, BookUpdateRequest req) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (req == null) {
            throw new RequestBodyMissingException();
        }
        if (req.bookName() == null && req.studentId() == null) {
            throw new EmptyUpdateRequestException();
        }

        if (req.bookName() != null) {
            book.setBookName(req.bookName());
        }
        if (req.studentId() != null) {
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new StudentNotFoundException(req.studentId()));
            book.setStudent(student);
        }

        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public BookResponse deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        bookRepository.delete(book);
        return bookMapper.toDto(book);
    }
}
