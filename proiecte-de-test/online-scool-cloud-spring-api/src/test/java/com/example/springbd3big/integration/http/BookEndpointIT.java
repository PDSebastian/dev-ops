package com.example.springbd3big.integration.http;

import com.example.springbd3big.book.dtos.BookCreateRequest;
import com.example.springbd3big.book.dtos.BookUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void createBookCreatesRecord() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        BookCreateRequest request = new BookCreateRequest("Domain Driven Design", (long) student.getId());

        mockMvc.perform(post("/books")
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bookName").value("Domain Driven Design"))
                .andExpect(jsonPath("$.studentId").value(student.getId()));
    }

    @Test
    void getAllBooksReturnsPersistedBooks() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        persistBook("Clean Code", student);

        mockMvc.perform(get("/books")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookName").value("Clean Code"));
    }

    @Test
    void getBookByIdReturnsBook() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var book = persistBook("Refactoring", student);

        mockMvc.perform(get("/books/{bookId}", book.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.studentId").value(student.getId()));
    }

    @Test
    void findBooksByStudentReturnsStudentBooks() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        persistBook("Patterns", student);

        mockMvc.perform(get("/books/find-by-student")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("studentId", String.valueOf(student.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(student.getId()));
    }

    @Test
    void searchBooksReturnsMatchingBooks() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        persistBook("Spring in Action", student);

        mockMvc.perform(get("/books/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookName").value("Spring in Action"));
    }

    @Test
    void updateBookUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var newStudent = persistStudent();
        var book = persistBook("Legacy Title", student);
        BookUpdateRequest request = new BookUpdateRequest("New Title", (long) newStudent.getId());

        mockMvc.perform(put("/books/{bookId}", book.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName").value("New Title"))
                .andExpect(jsonPath("$.studentId").value(newStudent.getId()));
    }

    @Test
    void patchBookUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var book = persistBook("Patched Title", student);
        BookUpdateRequest request = new BookUpdateRequest("Patched Book", null);

        mockMvc.perform(patch("/books/{bookId}", book.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName").value("Patched Book"))
                .andExpect(jsonPath("$.studentId").value(student.getId()));
    }

    @Test
    void deleteBookRemovesExistingRecord() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var book = persistBook("Delete Me", student);

        mockMvc.perform(delete("/books/{bookId}", book.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.bookName").value("Delete Me"));
    }
}
