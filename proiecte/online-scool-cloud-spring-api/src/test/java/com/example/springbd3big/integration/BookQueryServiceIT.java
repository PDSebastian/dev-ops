package com.example.springbd3big.integration;

import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.book.service.query.BookQueryService;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookQueryServiceIT {

    @Autowired
    private BookQueryService bookQueryService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Transactional
    void getAllBooks_returnsDtos() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Nico")
                .lastName("Marin")
                .email("nico.marin@test.com")
                .build());

        bookRepository.save(Book.builder()
                .bookName("Book One")
                .student(student)
                .build());
        bookRepository.save(Book.builder()
                .bookName("Book Two")
                .student(student)
                .build());

        List<BookResponse> books = bookQueryService.getAllBooks();

        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.bookName().equals("Book One")));
        assertTrue(books.stream().anyMatch(b -> b.bookName().equals("Book Two")));
    }

    @Test
    @Transactional
    void getBookById_returnsDto() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Iulia")
                .lastName("Pop")
                .email("iulia.pop@test.com")
                .build());

        Book saved = bookRepository.save(Book.builder()
                .bookName("Spring in Action")
                .student(student)
                .build());

        BookResponse response = bookQueryService.getBookById((long) saved.getId());

        assertEquals(saved.getId(), response.id());
        assertEquals("Spring in Action", response.bookName());
        assertEquals(student.getId(), response.studentId());
    }
}
