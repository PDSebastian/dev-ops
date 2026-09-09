package com.example.springbd3big.integration;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.query.CourseQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CourseQueryServiceIT {

    @Autowired
    private CourseQueryService courseQueryService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Transactional
    void getAllCourses_returnsDtos() {
        courseRepository.save(Course.builder()
                .courseName("Physics")
                .departament("Science")
                .build());
        courseRepository.save(Course.builder()
                .courseName("Literature")
                .departament("Arts")
                .build());

        List<CourseResponse> courses = courseQueryService.getAllCourses();

        assertEquals(2, courses.size());
        assertTrue(courses.stream().anyMatch(c -> c.courseName().equals("Physics")));
        assertTrue(courses.stream().anyMatch(c -> c.courseName().equals("Literature")));
    }

    @Test
    @Transactional
    void getCourseById_returnsDto() {
        Course saved = courseRepository.save(Course.builder()
                .courseName("Chemistry")
                .departament("Science")
                .build());

        CourseResponse response = courseQueryService.getCourseById((long) saved.getId());

        assertEquals(saved.getId(), response.id());
        assertEquals("Chemistry", response.courseName());
        assertEquals("Science", response.departament());
    }
}
