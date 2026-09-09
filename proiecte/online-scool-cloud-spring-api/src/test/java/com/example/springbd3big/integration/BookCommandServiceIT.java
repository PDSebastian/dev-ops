package com.example.springbd3big.integration;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.book.service.command.BookCommandService;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookCommandServiceIT {

    @Autowired
    private BookCommandService bookCommandService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Transactional
    void createBook_persistsAndReturnsDto() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Dana")
                .lastName("Iorga")
                .email("dana.iorga@test.com")
                .build());

        BookCreateRequest req = new BookCreateRequest("Clean Code", (long) student.getId());

        BookResponse created = bookCommandService.createBook(req);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("Clean Code", created.bookName());
        assertEquals(student.getId(), created.studentId());
        assertTrue(bookRepository.findById((long) created.id()).isPresent());
    }

    @Test
    @Transactional
    void updateBook_updatesFieldsAndReturnsDto() {
        Student student1 = studentRepository.save(Student.builder()
                .firstName("Alex")
                .lastName("Stan")
                .email("alex.stan@test.com")
                .build());
        Student student2 = studentRepository.save(Student.builder()
                .firstName("Mara")
                .lastName("Avram")
                .email("mara.avram@test.com")
                .build());

        Book saved = bookRepository.save(Book.builder()
                .bookName("Old Name")
                .student(student1)
                .build());

        BookUpdateRequest req = new BookUpdateRequest("New Name", (long) student2.getId());
        BookResponse updated = bookCommandService.updateBook((long) saved.getId(), req);

        assertNotNull(updated);
        assertEquals("New Name", updated.bookName());
        assertEquals(student2.getId(), updated.studentId());
    }

    @Test
    @Transactional
    void deleteBook_removesEntity() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Ioan")
                .lastName("Iliescu")
                .email("ioan.iliescu@test.com")
                .build());

        Book saved = bookRepository.save(Book.builder()
                .bookName("Delete Me")
                .student(student)
                .build());

        Long id = (long) saved.getId();
        BookResponse deleted = bookCommandService.deleteBook(id);

        assertNotNull(deleted);
        assertFalse(bookRepository.findById(id).isPresent());
    }
}
