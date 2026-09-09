package com.example.springbd3big.integration.http;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeacherEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllTeachersRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/teachers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTeachersRejectsUsersWithoutTeacherReadAuthority() throws Exception {
        var student = persistStudent();

        mockMvc.perform(get("/teachers")
                        .header("Authorization", bearerTokenFor(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllTeachersReturnsPersistedTeachers() throws Exception {
        var administrator = persistAdministrator();
        var teacher = persistTeacher();

        mockMvc.perform(get("/teachers")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(teacher.getEmail()))
                .andExpect(jsonPath("$[0].employeeCode").value(teacher.getEmployeeCode()));
    }

    @Test
    void getTeacherByIdReturnsTeacher() throws Exception {
        var administrator = persistAdministrator();
        var teacher = persistTeacher();

        mockMvc.perform(get("/teachers/{teacherId}", teacher.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher.getId()))
                .andExpect(jsonPath("$.email").value(teacher.getEmail()));
    }

    @Test
    void getTeacherByIdReturnsNotFoundForMissingTeacher() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/teachers/{teacherId}", 999_999L)
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("teacher with id 999999 not found"));
    }

    @Test
    void findTeacherByEmailReturnsTeacher() throws Exception {
        var administrator = persistAdministrator();
        var teacher = persistTeacher();

        mockMvc.perform(get("/teachers/find-by-email")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("email", teacher.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(teacher.getEmail()));
    }

    @Test
    void findTeacherByEmailReturnsNotFoundForUnknownEmail() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/teachers/find-by-email")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("email", "missing.teacher@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("teacher with email missing.teacher@example.com not found"));
    }

    @Test
    void searchTeachersReturnsMatchingTeachers() throws Exception {
        var administrator = persistAdministrator();
        var teacher = persistTeacher();

        mockMvc.perform(get("/teachers/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", teacher.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(teacher.getEmail()));
    }

    @Test
    void searchTeachersReturnsEmptyListForBlankQuery() throws Exception {
        var administrator = persistAdministrator();
        persistTeacher();

        mockMvc.perform(get("/teachers/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
