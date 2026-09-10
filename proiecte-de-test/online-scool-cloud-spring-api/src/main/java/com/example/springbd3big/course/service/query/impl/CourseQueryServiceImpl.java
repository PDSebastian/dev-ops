package com.example.springbd3big.course.service.query.impl;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.query.CourseQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CourseQueryServiceImpl implements CourseQueryService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseQueryServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper){
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;

    }
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return courseMapper.toDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findByDepartment(String department) {
        return courseRepository.findByDepartamentContainingIgnoreCase(department).stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        return courseRepository
                .findByCourseNameContainingIgnoreCaseOrDepartamentContainingIgnoreCase(normalized, normalized)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}
