package com.example.springbd3big.integration;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.command.CourseCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CourseCommandServiceIT {

    @Autowired
    private CourseCommandService courseCommandService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Transactional
    void createCourse_persistsAndReturnsDto() {
        CourseCreateRequest req = new CourseCreateRequest("History", "Humanities");

        CourseResponse created = courseCommandService.createCourse(req);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("History", created.courseName());
        assertEquals("Humanities", created.departament());
        assertTrue(courseRepository.findById((long) created.id()).isPresent());
    }

    @Test
    @Transactional
    void updateCourse_updatesFieldsAndReturnsDto() {
        Course saved = courseRepository.save(Course.builder()
                .courseName("Math")
                .departament("STEM")
                .build());

        CourseUpdateRequest req = new CourseUpdateRequest("Advanced Math", "Science");
        CourseResponse updated = courseCommandService.updateCourse((long) saved.getId(), req);

        assertNotNull(updated);
        assertEquals("Advanced Math", updated.courseName());
        assertEquals("Science", updated.departament());
    }

    @Test
    @Transactional
    void deleteCourse_removesEntity() {
        Course saved = courseRepository.save(Course.builder()
                .courseName("Biology")
                .departament("Science")
                .build());

        Long id = (long) saved.getId();
        CourseResponse deleted = courseCommandService.deleteCourse(id);

        assertNotNull(deleted);
        assertFalse(courseRepository.findById(id).isPresent());
    }
}
