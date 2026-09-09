package com.example.springbd3big.user.teacher.controller;

import com.example.springbd3big.user.teacher.dtos.TeacherResponse;
import com.example.springbd3big.user.teacher.service.query.TeacherQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/teachers")
@Tag(name = "Teachers", description = "Teacher query endpoints. Read operations require teacher:read.")
public class TeacherController {

    private final TeacherQueryService teacherQueryService;

    public TeacherController(TeacherQueryService teacherQueryService) {
        this.teacherQueryService = teacherQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all teachers", description = "Required permission: teacher:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<TeacherResponse> getAllTeachers() {
        return teacherQueryService.getAllTeachers();
    }

    @GetMapping("/{teacherId}")
    @Operation(summary = "Get teacher by id", description = "Required permission: teacher:read", security = @SecurityRequirement(name = "bearerAuth"))
    public TeacherResponse getTeacherById(@PathVariable @Positive Long teacherId) {
        return teacherQueryService.getTeacherById(teacherId);
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find teacher by email", description = "Required permission: teacher:read", security = @SecurityRequirement(name = "bearerAuth"))
    public TeacherResponse findByEmail(@RequestParam @NotBlank String email) {
        return teacherQueryService.findByEmail(email);
    }

    @GetMapping("/search")
    @Operation(summary = "Search teachers", description = "Required permission: teacher:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<TeacherResponse> searchTeachers(@RequestParam(name = "q", required = false) String query) {
        return teacherQueryService.searchTeachers(query);
    }
}
