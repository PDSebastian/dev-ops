package com.example.springbd3big.unit.controller;

import com.example.springbd3big.config.GlobalExceptionHandler;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.user.student.controller.StudentController;
import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.service.command.StudentCommandService;
import com.example.springbd3big.user.student.service.query.StudentQueryService;
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

class StudentControllerTest {

    private MockMvc mockMvc;
    private StubStudentCommandService studentCommandService;
    private StubStudentQueryService studentQueryService;

    @BeforeEach
    void setUp() {
        studentCommandService = new StubStudentCommandService();
        studentQueryService = new StubStudentQueryService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new StudentController(studentCommandService, studentQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createStudent_returnsCreatedStudent() throws Exception {
        studentCommandService.createResult = new StudentResponse(1, "Ion", "Popescu", "ion@test.com");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ion",
                                  "lastName": "Popescu",
                                  "email": "ion@test.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ion"))
                .andExpect(jsonPath("$.lastName").value("Popescu"))
                .andExpect(jsonPath("$.email").value("ion@test.com"));
    }

    @Test
    void createStudent_withInvalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "",
                                  "email": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllStudents_returnsStudentList() throws Exception {
        studentQueryService.allStudents = List.of(
                new StudentResponse(1, "Ion", "Popescu", "ion@test.com"),
                new StudentResponse(2, "Ana", "Ionescu", "ana@test.com")
        );

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ion"))
                .andExpect(jsonPath("$[0].lastName").value("Popescu"))
                .andExpect(jsonPath("$[0].email").value("ion@test.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Ana"))
                .andExpect(jsonPath("$[1].lastName").value("Ionescu"))
                .andExpect(jsonPath("$[1].email").value("ana@test.com"));
    }

    @Test
    void getStudentById_returnsStudent() throws Exception {
        studentQueryService.studentById = new StudentResponse(3, "Mara", "Georgescu", "mara@test.com");

        mockMvc.perform(get("/students/{studentId}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("Mara"))
                .andExpect(jsonPath("$.lastName").value("Georgescu"))
                .andExpect(jsonPath("$.email").value("mara@test.com"));
    }

    @Test
    void getStudentById_whenMissing_returnsNotFound() throws Exception {
        studentQueryService.getStudentByIdError = new StudentNotFoundException(99L);

        mockMvc.perform(get("/students/{studentId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Student not found with id=99"));
    }

    @Test
    void findByEmail_returnsStudent() throws Exception {
        studentQueryService.studentByEmail = new StudentResponse(4, "Ana", "Marin", "ana@test.com");

        mockMvc.perform(get("/students/find-by-email").param("email", "ana@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Marin"))
                .andExpect(jsonPath("$.email").value("ana@test.com"));
    }

    @Test
    void searchStudents_returnsStudentList() throws Exception {
        studentQueryService.searchResults = List.of(new StudentResponse(5, "Andrei", "Stan", "andrei@test.com"));

        mockMvc.perform(get("/students/search").param("q", "an"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(jsonPath("$[0].lastName").value("Stan"))
                .andExpect(jsonPath("$[0].email").value("andrei@test.com"));
    }

    @Test
    void getCoursesForStudent_returnsCourseList() throws Exception {
        studentQueryService.studentCourses = List.of(
                new CourseResponse(1, "Java", "CS"),
                new CourseResponse(2, "Databases", "IT")
        );

        mockMvc.perform(get("/students/{studentId}/courses", 6))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Java"))
                .andExpect(jsonPath("$[0].departament").value("CS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].courseName").value("Databases"))
                .andExpect(jsonPath("$[1].departament").value("IT"));
    }

    @Test
    void updateStudent_returnsUpdatedStudent() throws Exception {
        studentCommandService.updateResult = new StudentResponse(7, "Updated", "Student", "updated@test.com");

        mockMvc.perform(put("/students/{studentId}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Updated",
                                  "lastName": "Student",
                                  "email": "updated@test.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Student"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void patchStudent_returnsUpdatedStudent() throws Exception {
        studentCommandService.updateResult = new StudentResponse(8, "Patch", "Name", "patch@test.com");

        mockMvc.perform(patch("/students/{studentId}", 8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Patch",
                                  "lastName": "Name",
                                  "email": "patch@test.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.firstName").value("Patch"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("patch@test.com"));
    }

    @Test
    void deleteStudent_returnsDeletedStudent() throws Exception {
        studentCommandService.deleteResult = new StudentResponse(9, "Deleted", "Student", "deleted@test.com");

        mockMvc.perform(delete("/students/{studentId}", 9))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.firstName").value("Deleted"))
                .andExpect(jsonPath("$.lastName").value("Student"))
                .andExpect(jsonPath("$.email").value("deleted@test.com"));
    }

    private static final class StubStudentCommandService implements StudentCommandService {
        private StudentResponse createResult;
        private StudentResponse updateResult;
        private StudentResponse deleteResult;

        @Override
        public StudentResponse createStudent(StudentCreateRequest req) {
            return createResult;
        }

        @Override
        public StudentResponse updateStudent(Long studentId, StudentUpdateRequest req) {
            return updateResult;
        }

        @Override
        public StudentResponse deleteStudent(Long studentId) {
            return deleteResult;
        }
    }

    private static final class StubStudentQueryService implements StudentQueryService {
        private List<StudentResponse> allStudents = List.of();
        private StudentResponse studentById;
        private RuntimeException getStudentByIdError;
        private StudentResponse studentByEmail;
        private List<StudentResponse> searchResults = List.of();
        private List<CourseResponse> studentCourses = List.of();

        @Override
        public List<StudentResponse> getAllStudents() {
            return allStudents;
        }

        @Override
        public StudentResponse getStudentById(Long studentId) {
            if (getStudentByIdError != null) {
                throw getStudentByIdError;
            }
            return studentById;
        }

        @Override
        public StudentResponse findByEmail(String email) {
            return studentByEmail;
        }

        @Override
        public List<StudentResponse> searchStudents(String query) {
            return searchResults;
        }

        @Override
        public List<CourseResponse> getCoursesForStudent(Long studentId) {
            return studentCourses;
        }
    }
}
