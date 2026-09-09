package com.example.springbd3big.user.administrator.controller;

import com.example.springbd3big.user.administrator.dtos.AdministratorResponse;
import com.example.springbd3big.user.administrator.service.query.AdministratorQueryService;
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
@RequestMapping("/administrators")
@Tag(name = "Administrators", description = "Administrator query endpoints. Read operations require administrator:read.")
public class AdministratorController {

    private final AdministratorQueryService administratorQueryService;

    public AdministratorController(AdministratorQueryService administratorQueryService) {
        this.administratorQueryService = administratorQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all administrators", description = "Required permission: administrator:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<AdministratorResponse> getAllAdministrators() {
        return administratorQueryService.getAllAdministrators();
    }

    @GetMapping("/{administratorId}")
    @Operation(summary = "Get administrator by id", description = "Required permission: administrator:read", security = @SecurityRequirement(name = "bearerAuth"))
    public AdministratorResponse getAdministratorById(@PathVariable @Positive Long administratorId) {
        return administratorQueryService.getAdministratorById(administratorId);
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find administrator by email", description = "Required permission: administrator:read", security = @SecurityRequirement(name = "bearerAuth"))
    public AdministratorResponse findByEmail(@RequestParam @NotBlank String email) {
        return administratorQueryService.findByEmail(email);
    }

    @GetMapping("/search")
    @Operation(summary = "Search administrators", description = "Required permission: administrator:read", security = @SecurityRequirement(name = "bearerAuth"))
    public List<AdministratorResponse> searchAdministrators(@RequestParam(name = "q", required = false) String query) {
        return administratorQueryService.searchAdministrators(query);
    }
}
