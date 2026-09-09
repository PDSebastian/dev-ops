package com.example.springbd3big.unit.controller;

import com.example.springbd3big.config.GlobalExceptionHandler;
import com.example.springbd3big.course.controller.CourseController;
import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.service.command.CourseCommandService;
import com.example.springbd3big.course.service.query.CourseQueryService;
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

class CourseControllerTest {

    private MockMvc mockMvc;
    private StubCourseCommandService courseCommandService;
    private StubCourseQueryService courseQueryService;

    @BeforeEach
    void setUp() {
        courseCommandService = new StubCourseCommandService();
        courseQueryService = new StubCourseQueryService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new CourseController(courseCommandService, courseQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createCourse_returnsCreatedCourse() throws Exception {
        courseCommandService.createResult = new CourseResponse(1, "Algorithms", "CS");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "Algorithms",
                                  "departament": "CS"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.courseName").value("Algorithms"))
                .andExpect(jsonPath("$.departament").value("CS"));
    }

    @Test
    void createCourse_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "",
                                  "departament": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllCourses_returnsCourseList() throws Exception {
        courseQueryService.allCourses = List.of(
                new CourseResponse(1, "Algorithms", "CS"),
                new CourseResponse(2, "Databases", "IT")
        );

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Algorithms"))
                .andExpect(jsonPath("$[0].departament").value("CS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].courseName").value("Databases"))
                .andExpect(jsonPath("$[1].departament").value("IT"));
    }

    @Test
    void getCourseById_returnsCourse() throws Exception {
        courseQueryService.courseById = new CourseResponse(4, "Operating Systems", "CS");

        mockMvc.perform(get("/courses/{courseId}", 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.courseName").value("Operating Systems"))
                .andExpect(jsonPath("$.departament").value("CS"));
    }

    @Test
    void getCourseById_whenMissing_returnsNotFound() throws Exception {
        courseQueryService.getCourseByIdError = new CourseNotFoundException(77L);

        mockMvc.perform(get("/courses/{courseId}", 77))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Course not found with id=77"));
    }

    @Test
    void findByDepartment_returnsCourses() throws Exception {
        courseQueryService.coursesByDepartment = List.of(new CourseResponse(5, "Networks", "CS"));

        mockMvc.perform(get("/courses/find-by-department").param("department", "CS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].courseName").value("Networks"))
                .andExpect(jsonPath("$[0].departament").value("CS"));
    }

    @Test
    void searchCourses_returnsMatchingCourses() throws Exception {
        courseQueryService.searchResults = List.of(new CourseResponse(6, "Data Mining", "CS"));

        mockMvc.perform(get("/courses/search").param("q", "data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6))
                .andExpect(jsonPath("$[0].courseName").value("Data Mining"))
                .andExpect(jsonPath("$[0].departament").value("CS"));
    }

    @Test
    void updateCourse_returnsUpdatedCourse() throws Exception {
        courseCommandService.updateResult = new CourseResponse(8, "Advanced Java", "SE");

        mockMvc.perform(put("/courses/{courseId}", 8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "Advanced Java",
                                  "department": "SE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.courseName").value("Advanced Java"))
                .andExpect(jsonPath("$.departament").value("SE"));
    }

    @Test
    void patchCourse_returnsUpdatedCourse() throws Exception {
        courseCommandService.updateResult = new CourseResponse(9, "Cloud Computing", "IT");

        mockMvc.perform(patch("/courses/{courseId}", 9)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "Cloud Computing",
                                  "department": "IT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.courseName").value("Cloud Computing"))
                .andExpect(jsonPath("$.departament").value("IT"));
    }

    @Test
    void deleteCourse_returnsDeletedCourse() throws Exception {
        courseCommandService.deleteResult = new CourseResponse(10, "Deleted Course", "ARCH");

        mockMvc.perform(delete("/courses/{courseId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.courseName").value("Deleted Course"))
                .andExpect(jsonPath("$.departament").value("ARCH"));
    }

    private static final class StubCourseCommandService implements CourseCommandService {
        private CourseResponse createResult;
        private CourseResponse updateResult;
        private CourseResponse deleteResult;

        @Override
        public CourseResponse createCourse(CourseCreateRequest req) {
            return createResult;
        }

        @Override
        public CourseResponse updateCourse(Long courseId, CourseUpdateRequest req) {
            return updateResult;
        }

        @Override
        public CourseResponse deleteCourse(Long courseId) {
            return deleteResult;
        }
    }

    private static final class StubCourseQueryService implements CourseQueryService {
        private List<CourseResponse> allCourses = List.of();
        private CourseResponse courseById;
        private RuntimeException getCourseByIdError;
        private List<CourseResponse> coursesByDepartment = List.of();
        private List<CourseResponse> searchResults = List.of();

        @Override
        public List<CourseResponse> getAllCourses() {
            return allCourses;
        }

        @Override
        public CourseResponse getCourseById(Long courseId) {
            if (getCourseByIdError != null) {
                throw getCourseByIdError;
            }
            return courseById;
        }

        @Override
        public List<CourseResponse> findByDepartment(String department) {
            return coursesByDepartment;
        }

        @Override
        public List<CourseResponse> searchCourses(String query) {
            return searchResults;
        }
    }
}
