package com.example.springbd3big.unit.controller;

import com.example.springbd3big.config.GlobalExceptionHandler;
import com.example.springbd3big.enrolment.controller.EnrolmentController;
import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import com.example.springbd3big.enrolment.exceptions.EnrolmentNotFoundException;
import com.example.springbd3big.enrolment.service.command.EnrolmentCommandService;
import com.example.springbd3big.enrolment.service.query.EnrolmentQueryService;
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

class EnrolmentControllerTest {

    private MockMvc mockMvc;
    private StubEnrolmentCommandService enrolmentCommandService;
    private StubEnrolmentQueryService enrolmentQueryService;

    @BeforeEach
    void setUp() {
        enrolmentCommandService = new StubEnrolmentCommandService();
        enrolmentQueryService = new StubEnrolmentQueryService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new EnrolmentController(enrolmentCommandService, enrolmentQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createEnrolment_returnsCreatedEnrolment() throws Exception {
        enrolmentCommandService.createResult = new EnrolmentResponse(3, 5);

        mockMvc.perform(post("/enrolments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 3,
                                  "courseId": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(3))
                .andExpect(jsonPath("$.courseId").value(5));
    }

    @Test
    void createEnrolment_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/enrolments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": null,
                                  "courseId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllEnrolments_returnsEnrolmentList() throws Exception {
        enrolmentQueryService.allEnrolments = List.of(
                new EnrolmentResponse(1, 2),
                new EnrolmentResponse(3, 4)
        );

        mockMvc.perform(get("/enrolments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(2))
                .andExpect(jsonPath("$[1].studentId").value(3))
                .andExpect(jsonPath("$[1].courseId").value(4));
    }

    @Test
    void getEnrolmentById_returnsEnrolment() throws Exception {
        enrolmentQueryService.enrolmentById = new EnrolmentResponse(8, 9);

        mockMvc.perform(get("/enrolments/{enrolmentId}", 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(8))
                .andExpect(jsonPath("$.courseId").value(9));
    }

    @Test
    void getEnrolmentById_whenMissing_returnsNotFound() throws Exception {
        enrolmentQueryService.getEnrolmentByIdError = new EnrolmentNotFoundException(44L);

        mockMvc.perform(get("/enrolments/{enrolmentId}", 44))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Enrolment not found with id=44"));
    }

    @Test
    void findByStudent_returnsEnrolments() throws Exception {
        enrolmentQueryService.enrolmentsByStudent = List.of(new EnrolmentResponse(5, 10));

        mockMvc.perform(get("/enrolments/find-by-student").param("studentId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(5))
                .andExpect(jsonPath("$[0].courseId").value(10));
    }

    @Test
    void findByCourse_returnsEnrolments() throws Exception {
        enrolmentQueryService.enrolmentsByCourse = List.of(new EnrolmentResponse(11, 6));

        mockMvc.perform(get("/enrolments/find-by-course").param("courseId", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(11))
                .andExpect(jsonPath("$[0].courseId").value(6));
    }

    @Test
    void searchEnrolments_returnsEnrolments() throws Exception {
        enrolmentQueryService.searchResults = List.of(new EnrolmentResponse(7, 8));

        mockMvc.perform(get("/enrolments/search")
                        .param("studentId", "7")
                        .param("courseId", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(7))
                .andExpect(jsonPath("$[0].courseId").value(8));
    }

    @Test
    void updateEnrolment_returnsUpdatedEnrolment() throws Exception {
        enrolmentCommandService.updateResult = new EnrolmentResponse(12, 13);

        mockMvc.perform(put("/enrolments/{enrolmentId}", 9)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 12,
                                  "courseId": 13
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(12))
                .andExpect(jsonPath("$.courseId").value(13));
    }

    @Test
    void patchEnrolment_returnsUpdatedEnrolment() throws Exception {
        enrolmentCommandService.updateResult = new EnrolmentResponse(14, 15);

        mockMvc.perform(patch("/enrolments/{enrolmentId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 14,
                                  "courseId": 15
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(14))
                .andExpect(jsonPath("$.courseId").value(15));
    }

    @Test
    void deleteEnrolment_returnsDeletedEnrolment() throws Exception {
        enrolmentCommandService.deleteResult = new EnrolmentResponse(16, 17);

        mockMvc.perform(delete("/enrolments/{enrolmentId}", 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(16))
                .andExpect(jsonPath("$.courseId").value(17));
    }

    private static final class StubEnrolmentCommandService implements EnrolmentCommandService {
        private EnrolmentResponse createResult;
        private EnrolmentResponse updateResult;
        private EnrolmentResponse deleteResult;

        @Override
        public EnrolmentResponse enrollStudent(EnrolmentCreateRequest req) {
            return createResult;
        }

        @Override
        public EnrolmentResponse updateEnrolment(Long enrolmentId, EnrolmentUpdateRequest req) {
            return updateResult;
        }

        @Override
        public EnrolmentResponse deleteEnrolment(Long enrolmentId) {
            return deleteResult;
        }
    }

    private static final class StubEnrolmentQueryService implements EnrolmentQueryService {
        private List<EnrolmentResponse> allEnrolments = List.of();
        private EnrolmentResponse enrolmentById;
        private RuntimeException getEnrolmentByIdError;
        private List<EnrolmentResponse> enrolmentsByStudent = List.of();
        private List<EnrolmentResponse> enrolmentsByCourse = List.of();
        private List<EnrolmentResponse> searchResults = List.of();

        @Override
        public List<EnrolmentResponse> getAllEnrolments() {
            return allEnrolments;
        }

        @Override
        public EnrolmentResponse getEnrolmentById(Long enrolmentId) {
            if (getEnrolmentByIdError != null) {
                throw getEnrolmentByIdError;
            }
            return enrolmentById;
        }

        @Override
        public List<EnrolmentResponse> findByStudentId(Long studentId) {
            return enrolmentsByStudent;
        }

        @Override
        public List<EnrolmentResponse> findByCourseId(Long courseId) {
            return enrolmentsByCourse;
        }

        @Override
        public List<EnrolmentResponse> searchEnrolments(Long studentId, Long courseId) {
            return searchResults;
        }
    }
}
