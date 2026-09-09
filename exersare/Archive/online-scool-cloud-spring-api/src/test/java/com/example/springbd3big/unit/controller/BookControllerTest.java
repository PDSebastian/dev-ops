package com.example.springbd3big.unit.controller;

import com.example.springbd3big.book.controller.BookController;
import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import com.example.springbd3big.book.exceptions.BookNotFoundException;
import com.example.springbd3big.book.service.command.BookCommandService;
import com.example.springbd3big.book.service.query.BookQueryService;
import com.example.springbd3big.config.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerTest {

    private MockMvc mockMvc;
    private StubBookCommandService bookCommandService;
    private StubBookQueryService bookQueryService;

    @BeforeEach
    void setUp() {
        bookCommandService = new StubBookCommandService();
        bookQueryService = new StubBookQueryService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new BookController(bookCommandService, bookQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createBook_returnsCreatedBook() throws Exception {
        bookCommandService.createResult = new BookResponse(1, "Clean Code", 7);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookName": "Clean Code",
                                  "studentId": 7
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("Clean Code"))
                .andExpect(jsonPath("$.studentId").value(7));
    }

    @Test
    void createBook_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookName": "",
                                  "studentId": 7
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllBooks_returnsBookList() throws Exception {
        bookQueryService.allBooks = List.of(
                new BookResponse(1, "Clean Code", 7),
                new BookResponse(2, "Refactoring", 9)
        );

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("Clean Code"))
                .andExpect(jsonPath("$[0].studentId").value(7))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].bookName").value("Refactoring"))
                .andExpect(jsonPath("$[1].studentId").value(9));
    }

    @Test
    void getBookById_returnsBook() throws Exception {
        bookQueryService.bookById = new BookResponse(1, "Domain-Driven Design", 10);

        mockMvc.perform(get("/books/{bookId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookName").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.studentId").value(10));
    }

    @Test
    void getBookById_whenMissing_returnsNotFound() throws Exception {
        bookQueryService.getBookByIdError = new BookNotFoundException(99L);

        mockMvc.perform(get("/books/{bookId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book not found with id=99"));
    }

    @Test
    void findByStudent_returnsBooks() throws Exception {
        bookQueryService.booksByStudent = List.of(new BookResponse(1, "Spring in Action", 7));

        mockMvc.perform(get("/books/find-by-student").param("studentId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bookName").value("Spring in Action"))
                .andExpect(jsonPath("$[0].studentId").value(7));
    }

    @Test
    void searchBooks_returnsMatchingBooks() throws Exception {
        bookQueryService.searchResults = List.of(new BookResponse(3, "Clean Architecture", 11));

        mockMvc.perform(get("/books/search").param("q", "clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].bookName").value("Clean Architecture"))
                .andExpect(jsonPath("$[0].studentId").value(11));
    }

    @Test
    void updateBook_returnsUpdatedBook() throws Exception {
        bookCommandService.updateResult = new BookResponse(5, "Updated Book", 12);

        mockMvc.perform(put("/books/{bookId}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookName": "Updated Book",
                                  "studentId": 12
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.bookName").value("Updated Book"))
                .andExpect(jsonPath("$.studentId").value(12));
    }

    @Test
    void patchBook_returnsUpdatedBook() throws Exception {
        bookCommandService.updateResult = new BookResponse(6, "Patched Book", 13);

        mockMvc.perform(patch("/books/{bookId}", 6)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookName": "Patched Book",
                                  "studentId": 13
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.bookName").value("Patched Book"))
                .andExpect(jsonPath("$.studentId").value(13));
    }

    @Test
    void deleteBook_returnsDeletedBook() throws Exception {
        bookCommandService.deleteResult = new BookResponse(8, "Deleted Book", 14);

        mockMvc.perform(delete("/books/{bookId}", 8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.bookName").value("Deleted Book"))
                .andExpect(jsonPath("$.studentId").value(14));
    }

    private static final class StubBookCommandService implements BookCommandService {
        private BookResponse createResult;
        private BookResponse updateResult;
        private BookResponse deleteResult;

        @Override
        public BookResponse createBook(BookCreateRequest req) {
            return createResult;
        }

        @Override
        public BookResponse updateBook(Long bookId, BookUpdateRequest req) {
            return updateResult;
        }

        @Override
        public BookResponse deleteBook(Long bookId) {
            return deleteResult;
        }
    }

    private static final class StubBookQueryService implements BookQueryService {
        private List<BookResponse> allBooks = List.of();
        private BookResponse bookById;
        private RuntimeException getBookByIdError;
        private List<BookResponse> booksByStudent = List.of();
        private List<BookResponse> searchResults = List.of();

        @Override
        public List<BookResponse> getAllBooks() {
            return allBooks;
        }

        @Override
        public BookResponse getBookById(Long bookId) {
            if (getBookByIdError != null) {
                throw getBookByIdError;
            }
            return bookById;
        }

        @Override
        public List<BookResponse> findByStudentId(Long studentId) {
            return booksByStudent;
        }

        @Override
        public List<BookResponse> searchBooks(String query) {
            return searchResults;
        }
    }
}
