package com.example.springbd3big.integration.http;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseEndpointIT extends AbstractEndpointIntegrationTest {

    @Test
    void createCourseCreatesRecord() throws Exception {
        var administrator = persistAdministrator();
        CourseCreateRequest request = new CourseCreateRequest("Operating Systems", "Computer Science");

        mockMvc.perform(post("/courses")
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.courseName").value("Operating Systems"))
                .andExpect(jsonPath("$.departament").value("Computer Science"));
    }

    @Test
    void getAllCoursesReturnsPersistedCourses() throws Exception {
        var administrator = persistAdministrator();
        persistCourse("Databases", "Computer Science");

        mockMvc.perform(get("/courses")
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Databases"));
    }

    @Test
    void getCourseByIdReturnsCourse() throws Exception {
        var administrator = persistAdministrator();
        var course = persistCourse("Networks", "Computer Science");

        mockMvc.perform(get("/courses/{courseId}", course.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.courseName").value("Networks"));
    }

    @Test
    void findByDepartmentReturnsMatchingCourses() throws Exception {
        var administrator = persistAdministrator();
        persistCourse("Linear Algebra", "Mathematics");

        mockMvc.perform(get("/courses/find-by-department")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("department", "Math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departament").value("Mathematics"));
    }

    @Test
    void searchCoursesReturnsMatchingCourses() throws Exception {
        var administrator = persistAdministrator();
        persistCourse("Software Testing", "Computer Science");

        mockMvc.perform(get("/courses/search")
                        .header("Authorization", bearerTokenFor(administrator))
                        .param("q", "Testing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Software Testing"));
    }

    @Test
    void updateCourseUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var course = persistCourse("Old Course", "Old Department");
        CourseUpdateRequest request = new CourseUpdateRequest("New Course", "New Department");

        mockMvc.perform(put("/courses/{courseId}", course.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("New Course"))
                .andExpect(jsonPath("$.departament").value("New Department"));
    }

    @Test
    void patchCourseUsesRequestPayload() throws Exception {
        var administrator = persistAdministrator();
        var course = persistCourse("Patchable Course", "Initial Department");
        CourseUpdateRequest request = new CourseUpdateRequest(null, "Patched Department");

        mockMvc.perform(patch("/courses/{courseId}", course.getId())
                        .header("Authorization", bearerTokenFor(administrator))
                        .contentType(APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Patchable Course"))
                .andExpect(jsonPath("$.departament").value("Patched Department"));
    }

    @Test
    void deleteCourseRemovesExistingRecord() throws Exception {
        var administrator = persistAdministrator();
        var course = persistCourse("Delete Course", "Temporary");

        mockMvc.perform(delete("/courses/{courseId}", course.getId())
                        .header("Authorization", bearerTokenFor(administrator)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.courseName").value("Delete Course"));
    }
}
