package com.example.springbd3big.enrolment.service.query;

import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;

import java.util.List;

public interface EnrolmentQueryService {
    List<EnrolmentResponse> getAllEnrolments();
    EnrolmentResponse getEnrolmentById(Long enrolmentId);
    List<EnrolmentResponse> findByStudentId(Long studentId);
    List<EnrolmentResponse> findByCourseId(Long courseId);
    List<EnrolmentResponse> searchEnrolments(Long studentId, Long courseId);
}
