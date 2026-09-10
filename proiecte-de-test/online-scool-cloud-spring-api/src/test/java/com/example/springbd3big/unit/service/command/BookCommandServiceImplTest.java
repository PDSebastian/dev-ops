package com.example.springbd3big.unit.service.command;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import com.example.springbd3big.book.exceptions.BookNotFoundException;
import com.example.springbd3big.book.mappers.BookMapper;
import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.book.service.command.impl.BookCommandServiceImpl;
import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookCommandServiceImplTest {

    @Test
    void createBook_throwsWhenRequestMissing() {
        BookCommandServiceImpl service = new BookCommandServiceImpl(
                stub(BookRepository.class, Map.of()),
                stub(StudentRepository.class, Map.of()),
                new BookMapper()
        );

        assertThrows(RequestBodyMissingException.class, () -> service.createBook(null));
    }

    @Test
    void createBook_savesMappedBookWithoutStudentWhenStudentIdMissing() {
        AtomicReference<Book> savedBook = new AtomicReference<>();
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "save", args -> {
                    Book book = (Book) args[0];
                    savedBook.set(book);
                    book.setId(10);
                    return book;
                }
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of());

        BookResponse response = new BookCommandServiceImpl(bookRepository, studentRepository, new BookMapper())
                .createBook(new BookCreateRequest("Clean Code", null));

        assertEquals(10, response.id());
        assertEquals("Clean Code", response.bookName());
        assertNull(response.studentId());
        assertEquals("Clean Code", savedBook.get().getBookName());
        assertNull(savedBook.get().getStudent());
    }

    @Test
    void createBook_throwsWhenStudentIsMissing() {
        BookRepository bookRepository = stub(BookRepository.class, Map.of());
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new BookCommandServiceImpl(bookRepository, studentRepository, new BookMapper())
                        .createBook(new BookCreateRequest("Clean Code", 5L))
        );
    }

    @Test
    void createBook_attachesStudentAndReturnsDto() {
        Student student = Student.builder().firstName("Ana").lastName("Pop").email("ana@test.com").build();
        student.setId(7);
        AtomicReference<Book> savedBook = new AtomicReference<>();
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "save", args -> {
                    Book book = (Book) args[0];
                    savedBook.set(book);
                    book.setId(11);
                    return book;
                }
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        BookResponse response = new BookCommandServiceImpl(bookRepository, studentRepository, new BookMapper())
                .createBook(new BookCreateRequest("Refactoring", 7L));

        assertEquals(11, response.id());
        assertEquals("Refactoring", response.bookName());
        assertEquals(7, response.studentId());
        assertSame(student, savedBook.get().getStudent());
    }

    @Test
    void updateBook_throwsWhenBookMissing() {
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                BookNotFoundException.class,
                () -> new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                        .updateBook(1L, new BookUpdateRequest("New", null))
        );
    }

    @Test
    void updateBook_throwsWhenRequestMissing() {
        Book book = Book.builder().bookName("Old").build();
        book.setId(1);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));

        assertThrows(
                RequestBodyMissingException.class,
                () -> new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                        .updateBook(1L, null)
        );
    }

    @Test
    void updateBook_throwsWhenUpdateRequestIsEmpty() {
        Book book = Book.builder().bookName("Old").build();
        book.setId(1);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));

        assertThrows(
                EmptyUpdateRequestException.class,
                () -> new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                        .updateBook(1L, new BookUpdateRequest(null, null))
        );
    }

    @Test
    void updateBook_updatesBookNameOnly() {
        Book book = Book.builder().bookName("Old").build();
        book.setId(1);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));

        BookResponse response = new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                .updateBook(1L, new BookUpdateRequest("New", null));

        assertEquals("New", book.getBookName());
        assertEquals("New", response.bookName());
    }

    @Test
    void updateBook_throwsWhenStudentForUpdateIsMissing() {
        Book book = Book.builder().bookName("Old").build();
        book.setId(1);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new BookCommandServiceImpl(bookRepository, studentRepository, new BookMapper())
                        .updateBook(1L, new BookUpdateRequest(null, 8L))
        );
    }

    @Test
    void updateBook_updatesStudentWhenPresent() {
        Student student = Student.builder().firstName("M").lastName("N").email("m@n.com").build();
        student.setId(8);
        Book book = Book.builder().bookName("Old").build();
        book.setId(1);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        BookResponse response = new BookCommandServiceImpl(bookRepository, studentRepository, new BookMapper())
                .updateBook(1L, new BookUpdateRequest(null, 8L));

        assertSame(student, book.getStudent());
        assertEquals(8, response.studentId());
    }

    @Test
    void deleteBook_throwsWhenBookMissing() {
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                BookNotFoundException.class,
                () -> new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                        .deleteBook(3L)
        );
    }

    @Test
    void deleteBook_deletesBookAndReturnsDto() {
        AtomicReference<Book> deletedBook = new AtomicReference<>();
        Book book = Book.builder().bookName("Delete Me").build();
        book.setId(3);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book),
                "delete", args -> {
                    deletedBook.set((Book) args[0]);
                    return null;
                }
        ));

        BookResponse response = new BookCommandServiceImpl(bookRepository, stub(StudentRepository.class, Map.of()), new BookMapper())
                .deleteBook(3L);

        assertSame(book, deletedBook.get());
        assertEquals(3, response.id());
        assertEquals("Delete Me", response.bookName());
    }
}
