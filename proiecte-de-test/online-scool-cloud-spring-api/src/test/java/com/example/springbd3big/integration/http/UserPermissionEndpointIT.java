package com.example.springbd3big.integration.http;

import com.example.springbd3big.user.model.Permission;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserPermissionEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getUserPermissionsRejectsAnonymousRequests() throws Exception {
        var student = persistStudent();

        mockMvc.perform(get("/users/{userId}/permissions", student.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserPermissionsRejectsUsersWithoutUserReadAuthority() throws Exception {
        var teacher = persistTeacher();
        var student = persistStudent();

        mockMvc.perform(get("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(teacher)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserPermissionsReturnsAssignedPermissions() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(get("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(student.getId()))
                .andExpect(jsonPath("$.permissions", hasItem("student:read")))
                .andExpect(jsonPath("$.permissions", hasItem("book:write")));
    }

    @Test
    void getUserPermissionsReturnsNotFoundForMissingUser() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/users/{userId}/permissions", 999_999L)
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user with id 999999 not found"));
    }

    @Test
    void addPermissionsRejectsUsersWithoutUserWriteAuthority() throws Exception {
        var teacher = persistTeacher();
        var student = persistStudent();

        mockMvc.perform(post("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(teacher))
                        .contentType("application/json")
                        .content(json(new PermissionRequest(Set.of(Permission.PERMISSION_READ)))))
                .andExpect(status().isForbidden());
    }

    @Test
    void addPermissionsAddsNewPermissionsToUser() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(post("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType("application/json")
                        .content(json(new PermissionRequest(Set.of(Permission.PERMISSION_READ, Permission.PERMISSION_WRITE)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(student.getId()))
                .andExpect(jsonPath("$.permissions", hasItem("permission:read")))
                .andExpect(jsonPath("$.permissions", hasItem("permission:write")));
    }

    @Test
    void addPermissionsIsIdempotentWhenPermissionAlreadyExists() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(post("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType("application/json")
                        .content(json(new PermissionRequest(Set.of(Permission.BOOK_WRITE)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions", hasItem("book:write")))
                .andExpect(jsonPath("$.permissions.length()").value(12));
    }

    @Test
    void addPermissionsReturnsValidationFailureForEmptyPayload() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(post("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType("application/json")
                        .content(json(new PermissionRequest(Set.of()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void addPermissionsReturnsBadRequestForInvalidPermissionName() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(post("/users/{userId}/permissions", student.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType("application/json")
                        .content("""
                                {"permissions":["NOT_A_PERMISSION"]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"));
    }

    @Test
    void addPermissionsReturnsNotFoundForMissingUser() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(post("/users/{userId}/permissions", 999_999L)
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType("application/json")
                        .content(json(new PermissionRequest(Set.of(Permission.PERMISSION_READ)))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user with id 999999 not found"));
    }

    @Test
    void removePermissionRemovesExistingPermissionFromUser() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(delete("/users/{userId}/permissions/{permission}", student.getId(), Permission.BOOK_WRITE.name())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(student.getId()))
                .andExpect(jsonPath("$.permissions", not(hasItem("book:write"))))
                .andExpect(jsonPath("$.permissions", hasItem("student:read")));
    }

    @Test
    void removePermissionReturnsNotFoundForMissingUser() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(delete("/users/{userId}/permissions/{permission}", 999_999L, Permission.PERMISSION_READ.name())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user with id 999999 not found"));
    }

    @Test
    void removePermissionIsNoOpWhenUserDoesNotHaveIt() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(delete("/users/{userId}/permissions/{permission}", student.getId(), Permission.PERMISSION_WRITE.name())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions.length()").value(12))
                .andExpect(jsonPath("$.permissions", hasItem("student:read")))
                .andExpect(jsonPath("$.permissions", not(hasItem("permission:write"))));
    }

    @Test
    void removePermissionReturnsBadRequestForInvalidPermissionName() throws Exception {
        var administrator = persistAdministrator();
        var student = persistStudent();

        mockMvc.perform(delete("/users/{userId}/permissions/{permission}", student.getId(), "NOT_A_PERMISSION")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for parameter: permission"));
    }

    private record PermissionRequest(Set<Permission> permissions) {
    }
}
