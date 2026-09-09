package com.example.springbd3big.user.student.service.command;

import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;

public interface StudentCommandService {

    StudentResponse createStudent(StudentCreateRequest req);
    StudentResponse updateStudent(Long studentId, StudentUpdateRequest req);
    StudentResponse deleteStudent(Long studentId);
}
