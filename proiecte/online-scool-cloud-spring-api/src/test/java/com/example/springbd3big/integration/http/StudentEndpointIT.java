package com.example.springbd3big.integration.http;

import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void createStudentCreatesRecord() throws Exception {
        var administrator = persistAdministrator();
        StudentCreateRequest request = new StudentCreateRequest("Mara", "Ionescu", "mara.ionescu@example.com");

        mockMvc.perform(post("/students")
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Mara"))
                .andExpect(jsonPath("$.lastName").value("Ionescu"))
                .andExpect(jsonPath("$.email").value("mara.ionescu@example.com"));
    }

    @Test
    void getAllStudentsReturnsPersistedStudents() throws Exception {
        var administrator = persistAdministrator();
        persistStudent();

        mockMvc.perform(get("/students")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").exists());
    }

    @Test
    void getStudentByIdReturnsStudent() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(get("/students/{studentId}", student.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }

    @Test
    void findStudentByEmailReturnsStudent() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(get("/students/find-by-email")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("email", student.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }

    @Test
    void searchStudentsReturnsMatchingEntries() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(get("/students/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", student.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(student.getEmail()));
    }

    @Test
    void getCoursesForStudentReturnsMappedCourses() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Algorithms", "Computer Science");
        persistEnrolment(student, course);

        mockMvc.perform(get("/students/{studentId}/courses", student.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(course.getId()))
                .andExpect(jsonPath("$[0].courseName").value("Algorithms"));
    }

    @Test
    void updateStudentUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        StudentUpdateRequest request = new StudentUpdateRequest("Updated", "Student", "updated.student@example.com");

        mockMvc.perform(put("/students/{studentId}", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Student"))
                .andExpect(jsonPath("$.email").value("updated.student@example.com"));
    }

    @Test
    void patchStudentUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        StudentUpdateRequest request = new StudentUpdateRequest(null, "Patched", "patched.student@example.com");

        mockMvc.perform(patch("/students/{studentId}", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()))
                .andExpect(jsonPath("$.lastName").value("Patched"))
                .andExpect(jsonPath("$.email").value("patched.student@example.com"));
    }

    @Test
    void deleteStudentRemovesExistingRecord() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(delete("/students/{studentId}", student.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }
}
