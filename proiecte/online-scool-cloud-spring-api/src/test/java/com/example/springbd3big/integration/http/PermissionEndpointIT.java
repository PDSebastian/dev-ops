package com.example.springbd3big.integration.http;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllPermissionsRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/permissions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllPermissionsRejectsUsersWithoutPermissionReadAuthority() throws Exception {
        var teacher = persistTeacher();

        mockMvc.perform(get("/permissions")
                        .header("Authorization", bearerTokenFor(teacher)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllPermissionsReturnsPermissionCatalog() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/permissions")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("PERMISSION_READ")))
                .andExpect(jsonPath("$[*].authority", hasItem("permission:write")));
    }

    @Test
    void getAllPermissionsDoesNotExposeRemovedRoleAuthorities() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/permissions")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.not(hasItem("ROLE_READ"))))
                .andExpect(jsonPath("$[*].authority").value(org.hamcrest.Matchers.not(hasItem("role:read"))));
    }
}
