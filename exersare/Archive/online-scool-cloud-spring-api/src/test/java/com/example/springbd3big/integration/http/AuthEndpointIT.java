package com.example.springbd3big.integration.http;

import com.example.springbd3big.auth.dtos.AuthRequest;
import com.example.springbd3big.auth.dtos.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void registerCreatesUserAndReturnsToken() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Ana",
                "Popescu",
                "ana.register@example.com",
                DEFAULT_PASSWORD,
                "+40740000000"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("ana.register@example.com"))
                .andExpect(jsonPath("$.user.permissions").isArray())
                .andExpect(jsonPath("$.user.permissions", hasItem("auth:read")))
                .andExpect(jsonPath("$.user.permissions", hasItem("student:read")))
                .andExpect(jsonPath("$.user.permissions", hasItem("book:write")))
                .andExpect(jsonPath("$.user.active").value(true));
    }

    @Test
    void registerAssignsOnlyStudentDefaultPermissions() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Mara",
                "Ionescu",
                "mara.register@example.com",
                DEFAULT_PASSWORD,
                "+40740000055"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.permissions", hasItem("student:read")))
                .andExpect(jsonPath("$.user.permissions", hasItem("course:read")))
                .andExpect(jsonPath("$.user.permissions", hasItem("dashboard:read")))
                .andExpect(jsonPath("$.user.permissions").value(org.hamcrest.Matchers.not(hasItem("teacher:write"))))
                .andExpect(jsonPath("$.user.permissions").value(org.hamcrest.Matchers.not(hasItem("permission:write"))));
    }

    @Test
    void loginAuthenticatesExistingUser() throws Exception {
        persistAdministrator();
        AuthRequest request = new AuthRequest("admin-admin-1@example.com", DEFAULT_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.user.email").value("admin-admin-1@example.com"))
                .andExpect(jsonPath("$.user.permissions").isArray());
    }

    @Test
    void meReturnsAuthenticatedUser() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(administrator.getEmail()))
                .andExpect(jsonPath("$.permissions").isArray());
    }

    @Test
    void meRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
