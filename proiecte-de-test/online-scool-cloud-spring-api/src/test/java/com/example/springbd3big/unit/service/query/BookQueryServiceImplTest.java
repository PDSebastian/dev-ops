package com.example.springbd3big.unit.service.query;

import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.exceptions.BookNotFoundException;
import com.example.springbd3big.book.mappers.BookMapper;
import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.book.service.query.impl.BookQueryServiceImpl;
import com.example.springbd3big.user.student.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookQueryServiceImplTest {

    @Test
    void getAllBooks_mapsAllEntities() {
        Student student = Student.builder().firstName("Ana").lastName("A").email("a@test.com").build();
        student.setId(4);
        Book book = Book.builder().bookName("Clean Code").student(student).build();
        book.setId(1);

        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findAll", args -> List.of(book)
        ));

        List<BookResponse> responses = new BookQueryServiceImpl(bookRepository, new BookMapper()).getAllBooks();

        assertEquals(1, responses.size());
        assertEquals("Clean Code", responses.getFirst().bookName());
        assertEquals(4, responses.getFirst().studentId());
    }

    @Test
    void getBookById_throwsWhenMissing() {
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(BookNotFoundException.class, () -> new BookQueryServiceImpl(bookRepository, new BookMapper()).getBookById(1L));
    }

    @Test
    void getBookById_returnsMappedBook() {
        Book book = Book.builder().bookName("DDD").build();
        book.setId(2);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findById", args -> Optional.of(book)
        ));

        BookResponse response = new BookQueryServiceImpl(bookRepository, new BookMapper()).getBookById(2L);

        assertEquals(2, response.id());
        assertEquals("DDD", response.bookName());
    }

    @Test
    void findByStudentId_mapsRepositoryResults() {
        Book book = Book.builder().bookName("Spring").build();
        book.setId(3);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findByStudentId", args -> List.of(book)
        ));

        List<BookResponse> responses = new BookQueryServiceImpl(bookRepository, new BookMapper()).findByStudentId(7L);

        assertEquals(1, responses.size());
        assertEquals("Spring", responses.getFirst().bookName());
    }

    @Test
    void searchBooks_returnsEmptyWhenQueryIsNull() {
        AtomicReference<String> calledWith = new AtomicReference<>("not-called");
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findByBookNameContainingIgnoreCase", args -> {
                    calledWith.set((String) args[0]);
                    return List.of();
                }
        ));

        List<BookResponse> responses = new BookQueryServiceImpl(bookRepository, new BookMapper()).searchBooks(null);

        assertTrue(responses.isEmpty());
        assertEquals("not-called", calledWith.get());
    }

    @Test
    void searchBooks_returnsEmptyWhenQueryIsBlank() {
        List<BookResponse> responses = new BookQueryServiceImpl(
                stub(BookRepository.class, Map.of()),
                new BookMapper()
        ).searchBooks("   ");

        assertTrue(responses.isEmpty());
    }

    @Test
    void searchBooks_trimsAndSearches() {
        AtomicReference<String> calledWith = new AtomicReference<>();
        Book book = Book.builder().bookName("Refactoring").build();
        book.setId(5);
        BookRepository bookRepository = stub(BookRepository.class, Map.of(
                "findByBookNameContainingIgnoreCase", args -> {
                    calledWith.set((String) args[0]);
                    return List.of(book);
                }
        ));

        List<BookResponse> responses = new BookQueryServiceImpl(bookRepository, new BookMapper()).searchBooks("  ref  ");

        assertEquals("ref", calledWith.get());
        assertEquals(1, responses.size());
        assertEquals("Refactoring", responses.getFirst().bookName());
    }
}
