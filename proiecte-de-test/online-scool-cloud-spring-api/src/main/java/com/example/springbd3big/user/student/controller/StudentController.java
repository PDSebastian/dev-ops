package com.example.springbd3big.user.student.controller;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.user.student.service.command.StudentCommandService;
import com.example.springbd3big.user.student.service.query.StudentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/students")
@Tag(name = "Students", description = "Student management endpoints. Read operations require student:read. Write operations require student:write.")
public class StudentController {

    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;

    public StudentController(
            StudentCommandService studentCommandService,
            StudentQueryService studentQueryService
    ) {
        this.studentCommandService = studentCommandService;
        this.studentQueryService = studentQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create student", description = "Required permission: student:write", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse createStudent(@Valid @RequestBody StudentCreateRequest request) {
        return studentCommandService.createStudent(request);
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Required permission: student:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<StudentResponse> getAllStudents() {
        return studentQueryService.getAllStudents();
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by id", description = "Required permission: student:read", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse getStudentById(@PathVariable @Positive Long studentId) {
        return studentQueryService.getStudentById(studentId);
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find student by email", description = "Required permission: student:read", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse findByEmail(@RequestParam @NotBlank String email) {
        return studentQueryService.findByEmail(email);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Required permission: student:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<StudentResponse> searchStudents(@RequestParam(name = "q", required = false) String query) {
        return studentQueryService.searchStudents(query);
    }

    @GetMapping("/{studentId}/courses")
    @Operation(summary = "Get courses for student", description = "Required permission: student:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<CourseResponse> getCoursesForStudent(@PathVariable @Positive Long studentId) {
        return studentQueryService.getCoursesForStudent(studentId);
    }

    @PutMapping("/{studentId}")
    @Operation(summary = "Update student", description = "Required permission: student:write", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse updateStudent(
            @PathVariable @Positive Long studentId,
            @RequestBody StudentUpdateRequest request
    ) {
        return studentCommandService.updateStudent(studentId, request);
    }

    @PatchMapping("/{studentId}")
    @Operation(summary = "Patch student", description = "Required permission: student:write", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse patchStudent(
            @PathVariable @Positive Long studentId,
            @RequestBody StudentUpdateRequest request
    ) {
        return studentCommandService.updateStudent(studentId, request);
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete student", description = "Required permission: student:write", security = @SecurityRequirement(name = "bearerAuth"))
    public StudentResponse deleteStudent(@PathVariable @Positive Long studentId) {
        return studentCommandService.deleteStudent(studentId);
    }
}
