package com.example.springbd3big.integration.http;

import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnrolmentEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void createEnrolmentCreatesRecord() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Distributed Systems", "Computer Science");
        EnrolmentCreateRequest request = new EnrolmentCreateRequest((long) student.getId(), (long) course.getId());

        mockMvc.perform(post("/enrolments")
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.courseId").value(course.getId()));
    }

    @Test
    void getAllEnrolmentsReturnsPersistedEnrolments() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Compilers", "Computer Science");
        persistEnrolment(student, course);

        mockMvc.perform(get("/enrolments")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(student.getId()))
                .andExpect(jsonPath("$[0].courseId").value(course.getId()));
    }

    @Test
    void getEnrolmentByIdReturnsEnrolment() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("AI", "Computer Science");
        var enrolment = persistEnrolment(student, course);

        mockMvc.perform(get("/enrolments/{enrolmentId}", enrolment.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.courseId").value(course.getId()));
    }

    @Test
    void findByStudentReturnsStudentEnrolments() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Machine Learning", "Computer Science");
        persistEnrolment(student, course);

        mockMvc.perform(get("/enrolments/find-by-student")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("studentId", String.valueOf(student.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(student.getId()));
    }

    @Test
    void findByCourseReturnsCourseEnrolments() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Security", "Computer Science");
        persistEnrolment(student, course);

        mockMvc.perform(get("/enrolments/find-by-course")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("courseId", String.valueOf(course.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(course.getId()));
    }

    @Test
    void searchEnrolmentsFiltersByStudentAndCourse() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("DevOps", "Computer Science");
        persistEnrolment(student, course);

        mockMvc.perform(get("/enrolments/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("studentId", String.valueOf(student.getId()))
                        .param("courseId", String.valueOf(course.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(student.getId()))
                .andExpect(jsonPath("$[0].courseId").value(course.getId()));
    }

    @Test
    void updateEnrolmentUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var newStudent = persistStudent();
        var course = persistCourse("Cloud", "Computer Science");
        var newCourse = persistCourse("Mobile", "Computer Science");
        var enrolment = persistEnrolment(student, course);
        EnrolmentUpdateRequest request = new EnrolmentUpdateRequest((long) newStudent.getId(), (long) newCourse.getId());

        mockMvc.perform(put("/enrolments/{enrolmentId}", enrolment.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(newStudent.getId()))
                .andExpect(jsonPath("$.courseId").value(newCourse.getId()));
    }

    @Test
    void patchEnrolmentUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Graphics", "Computer Science");
        var newCourse = persistCourse("UX", "Design");
        var enrolment = persistEnrolment(student, course);
        EnrolmentUpdateRequest request = new EnrolmentUpdateRequest(null, (long) newCourse.getId());

        mockMvc.perform(patch("/enrolments/{enrolmentId}", enrolment.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.courseId").value(newCourse.getId()));
    }

    @Test
    void deleteEnrolmentRemovesExistingRecord() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();
        var course = persistCourse("Architecture", "Computer Science");
        var enrolment = persistEnrolment(student, course);

        mockMvc.perform(delete("/enrolments/{enrolmentId}", enrolment.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.getId()))
                .andExpect(jsonPath("$.courseId").value(course.getId()));
    }
}
