package com.example.springbd3big.integration.http;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdministratorEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void getAllAdministratorsRejectsAnonymousRequests() throws Exception {
        mockMvc.perform(get("/administrators"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllAdministratorsRejectsUsersWithoutAdministratorReadAuthority() throws Exception {
        var teacher = persistTeacher();

        mockMvc.perform(get("/administrators")
                        .header("Authorization", bearerTokenFor(teacher)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllAdministratorsReturnsPersistedAdministrators() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(administrator.getEmail()))
                .andExpect(jsonPath("$[0].employeeCode").value(administrator.getEmployeeCode()));
    }

    @Test
    void getAdministratorByIdReturnsAdministrator() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/{administratorId}", administrator.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(administrator.getId()))
                .andExpect(jsonPath("$.email").value(administrator.getEmail()));
    }

    @Test
    void getAdministratorByIdReturnsNotFoundForMissingAdministrator() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/{administratorId}", 999_999L)
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("administrator with id 999999 not found"));
    }

    @Test
    void findAdministratorByEmailReturnsAdministrator() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/find-by-email")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("email", administrator.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(administrator.getEmail()));
    }

    @Test
    void findAdministratorByEmailReturnsNotFoundForUnknownEmail() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/find-by-email")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("email", "missing.admin@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("administrator with email missing.admin@example.com not found"));
    }

    @Test
    void searchAdministratorsReturnsMatchingAdministrators() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", administrator.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(administrator.getEmail()));
    }

    @Test
    void searchAdministratorsReturnsEmptyListForBlankQuery() throws Exception {
        var administrator = persistAdministrator();

        mockMvc.perform(get("/administrators/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
