package com.example.springbd3big.enrolment.controller;

import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import com.example.springbd3big.enrolment.service.command.EnrolmentCommandService;
import com.example.springbd3big.enrolment.service.query.EnrolmentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/enrolments")
@Tag(name = "Enrolments", description = "Enrolment management endpoints. Read operations require enrolment:read. Write operations require enrolment:write.")
public class EnrolmentController {

    private final EnrolmentCommandService enrolmentCommandService;
    private final EnrolmentQueryService enrolmentQueryService;

    public EnrolmentController(EnrolmentCommandService enrolmentCommandService, EnrolmentQueryService enrolmentQueryService) {
        this.enrolmentCommandService = enrolmentCommandService;
        this.enrolmentQueryService = enrolmentQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create enrolment", description = "Required permission: enrolment:write", security = @SecurityRequirement(name = "bearerAuth"))
    public EnrolmentResponse createEnrolment(@Valid @RequestBody EnrolmentCreateRequest request) {
        return enrolmentCommandService.enrollStudent(request);
    }

    @GetMapping
    @Operation(summary = "Get all enrolments", description = "Required permission: enrolment:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<EnrolmentResponse> getAllEnrolments() {
        return enrolmentQueryService.getAllEnrolments();
    }

    @GetMapping("/{enrolmentId}")
    @Operation(summary = "Get enrolment by id", description = "Required permission: enrolment:read", security = @SecurityRequirement(name = "bearerAuth"))
    public EnrolmentResponse getEnrolmentById(@PathVariable @Positive Long enrolmentId) {
        return enrolmentQueryService.getEnrolmentById(enrolmentId);
    }

    @GetMapping("/find-by-student")
    @Operation(summary = "Find enrolments by student", description = "Required permission: enrolment:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<EnrolmentResponse> findByStudent(@RequestParam @Positive Long studentId) {
        return enrolmentQueryService.findByStudentId(studentId);
    }

    @GetMapping("/find-by-course")
    @Operation(summary = "Find enrolments by course", description = "Required permission: enrolment:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<EnrolmentResponse> findByCourse(@RequestParam @Positive Long courseId) {
        return enrolmentQueryService.findByCourseId(courseId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search enrolments", description = "Required permission: enrolment:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<EnrolmentResponse> searchEnrolments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId
    ) {
        return enrolmentQueryService.searchEnrolments(studentId, courseId);
    }

    @PutMapping("/{enrolmentId}")
    @Operation(summary = "Update enrolment", description = "Required permission: enrolment:write", security = @SecurityRequirement(name = "bearerAuth"))
    public EnrolmentResponse updateEnrolment(
            @PathVariable @Positive Long enrolmentId,
            @RequestBody EnrolmentUpdateRequest request
    ) {
        return enrolmentCommandService.updateEnrolment(enrolmentId, request);
    }

    @PatchMapping("/{enrolmentId}")
    @Operation(summary = "Patch enrolment", description = "Required permission: enrolment:write", security = @SecurityRequirement(name = "bearerAuth"))
    public EnrolmentResponse patchEnrolment(
            @PathVariable @Positive Long enrolmentId,
            @RequestBody EnrolmentUpdateRequest request
    ) {
        return enrolmentCommandService.updateEnrolment(enrolmentId, request);
    }

    @DeleteMapping("/{enrolmentId}")
    @Operation(summary = "Delete enrolment", description = "Required permission: enrolment:write", security = @SecurityRequirement(name = "bearerAuth"))
    public EnrolmentResponse deleteEnrolment(@PathVariable @Positive Long enrolmentId) {
        return enrolmentCommandService.deleteEnrolment(enrolmentId);
    }
}
