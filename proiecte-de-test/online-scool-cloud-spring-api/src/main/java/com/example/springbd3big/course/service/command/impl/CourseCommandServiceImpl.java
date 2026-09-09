package com.example.springbd3big.course.service.command.impl;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.command.CourseCommandService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CourseCommandServiceImpl implements CourseCommandService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseCommandServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest req) {
        Course saved = courseRepository.save(courseMapper.toEntity(req));
        return courseMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (req == null) {
            throw new RequestBodyMissingException();
        }
        if (req.courseName() == null && req.department() == null) {
            throw new EmptyUpdateRequestException();
        }

        if (req.courseName() != null) {
            course.setCourseName(req.courseName());
        }
        if (req.department() != null) {
            course.setDepartament(req.department());
        }

        return courseMapper.toDto(course);
    }

    @Override
    @Transactional
    public CourseResponse deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        courseRepository.delete(course);
        return courseMapper.toDto(course);
    }
}
