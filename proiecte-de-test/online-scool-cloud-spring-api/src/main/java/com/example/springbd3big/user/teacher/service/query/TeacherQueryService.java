package com.example.springbd3big.user.teacher.service.query;

import com.example.springbd3big.user.teacher.dtos.TeacherResponse;

import java.util.List;

public interface TeacherQueryService {

    List<TeacherResponse> getAllTeachers();

    TeacherResponse getTeacherById(Long teacherId);

    TeacherResponse findByEmail(String email);

    List<TeacherResponse> searchTeachers(String query);
}
