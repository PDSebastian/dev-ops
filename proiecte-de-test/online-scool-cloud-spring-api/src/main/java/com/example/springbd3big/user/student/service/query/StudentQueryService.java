package com.example.springbd3big.user.student.service.query;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.user.student.dtos.StudentResponse;

import java.util.List;

public interface StudentQueryService {

    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long studentId);
    StudentResponse findByEmail(String email);
    List<StudentResponse> searchStudents(String query);
    List<CourseResponse> getCoursesForStudent(Long studentId);
}
