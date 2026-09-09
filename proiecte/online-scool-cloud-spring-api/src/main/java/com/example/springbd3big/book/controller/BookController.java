package com.example.springbd3big.book.controller;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookResponse;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import com.example.springbd3big.book.service.command.BookCommandService;
import com.example.springbd3big.book.service.query.BookQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management endpoints. Read operations require book:read. Write operations require book:write.")
public class BookController {

    private final BookCommandService bookCommandService;
    private final BookQueryService bookQueryService;

    public BookController(BookCommandService bookCommandService, BookQueryService bookQueryService) {
        this.bookCommandService = bookCommandService;
        this.bookQueryService = bookQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create book", description = "Required permission: book:write", security = @SecurityRequirement(name = "bearerAuth"))
    public BookResponse createBook(@Valid @RequestBody BookCreateRequest request) {
        return bookCommandService.createBook(request);
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Required permission: book:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<BookResponse> getAllBooks() {
        return bookQueryService.getAllBooks();
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Get book by id", description = "Required permission: book:read", security = @SecurityRequirement(name = "bearerAuth"))
    public BookResponse getBookById(@PathVariable @Positive Long bookId) {
        return bookQueryService.getBookById(bookId);
    }

    @GetMapping("/find-by-student")
    @Operation(summary = "Find books by student", description = "Required permission: book:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<BookResponse> findByStudent(@RequestParam @Positive Long studentId) {
        return bookQueryService.findByStudentId(studentId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Required permission: book:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<BookResponse> searchBooks(@RequestParam(name = "q", required = false) String query) {
        return bookQueryService.searchBooks(query);
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "Update book", description = "Required permission: book:write", security = @SecurityRequirement(name = "bearerAuth"))
    public BookResponse updateBook(@PathVariable @Positive Long bookId, @RequestBody BookUpdateRequest request) {
        return bookCommandService.updateBook(bookId, request);
    }

    @PatchMapping("/{bookId}")
    @Operation(summary = "Patch book", description = "Required permission: book:write", security = @SecurityRequirement(name = "bearerAuth"))
    public BookResponse patchBook(@PathVariable @Positive Long bookId, @RequestBody BookUpdateRequest request) {
        return bookCommandService.updateBook(bookId, request);
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Delete book", description = "Required permission: book:write", security = @SecurityRequirement(name = "bearerAuth"))
    public BookResponse deleteBook(@PathVariable @Positive Long bookId) {
        return bookCommandService.deleteBook(bookId);
    }
}
