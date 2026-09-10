package com.example.springbd3big.course.controller;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import com.example.springbd3big.course.service.command.CourseCommandService;
import com.example.springbd3big.course.service.query.CourseQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Course management endpoints. Read operations require course:read. Write operations require course:write.")
public class CourseController {

    private final CourseCommandService courseCommandService;
    private final CourseQueryService courseQueryService;

    public CourseController(CourseCommandService courseCommandService, CourseQueryService courseQueryService) {
        this.courseCommandService = courseCommandService;
        this.courseQueryService = courseQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create course", description = "Required permission: course:write", security = @SecurityRequirement(name = "bearerAuth"))
    public CourseResponse createCourse(@Valid @RequestBody CourseCreateRequest request) {
        return courseCommandService.createCourse(request);
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Required permission: course:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<CourseResponse> getAllCourses() {
        return courseQueryService.getAllCourses();
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course by id", description = "Required permission: course:read", security = @SecurityRequirement(name = "bearerAuth"))
    public CourseResponse getCourseById(@PathVariable @Positive Long courseId) {
        return courseQueryService.getCourseById(courseId);
    }

    @GetMapping("/find-by-department")
    @Operation(summary = "Find courses by department", description = "Required permission: course:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<CourseResponse> findByDepartment(@RequestParam @NotBlank String department) {
        return courseQueryService.findByDepartment(department);
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Required permission: course:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<CourseResponse> searchCourses(@RequestParam(name = "q", required = false) String query) {
        return courseQueryService.searchCourses(query);
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Update course", description = "Required permission: course:write", security = @SecurityRequirement(name = "bearerAuth"))
    public CourseResponse updateCourse(
            @PathVariable @Positive Long courseId,
            @RequestBody CourseUpdateRequest request
    ) {
        return courseCommandService.updateCourse(courseId, request);
    }

    @PatchMapping("/{courseId}")
    @Operation(summary = "Patch course", description = "Required permission: course:write", security = @SecurityRequirement(name = "bearerAuth"))
    public CourseResponse patchCourse(
            @PathVariable @Positive Long courseId,
            @RequestBody CourseUpdateRequest request
    ) {
        return courseCommandService.updateCourse(courseId, request);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete course", description = "Required permission: course:write", security = @SecurityRequirement(name = "bearerAuth"))
    public CourseResponse deleteCourse(@PathVariable @Positive Long courseId) {
        return courseCommandService.deleteCourse(courseId);
    }
}
