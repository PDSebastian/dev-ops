package com.example.springbd3big.course.service.command;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;

public interface CourseCommandService {

    CourseResponse createCourse(CourseCreateRequest req);
    CourseResponse updateCourse(Long courseId, CourseUpdateRequest req);
    CourseResponse deleteCourse(Long courseId);
}
