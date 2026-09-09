package com.example.springbd3big.course.exceptions;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String courseName, String department) {
        super("Course already exists with name=" + courseName + " and department=" + department);
    }
}
