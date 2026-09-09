package com.example.springbd3big.enrolment.service.query.impl;

import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.exceptions.EnrolmentNotFoundException;
import com.example.springbd3big.enrolment.mappers.EnrolmentMapper;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.query.EnrolmentQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EnrolmentQueryServiceImpl implements EnrolmentQueryService {

    private final EnrolmentRepository enrolmentRepository;
    private final EnrolmentMapper enrolmentMapper;

    public EnrolmentQueryServiceImpl(EnrolmentRepository enrolmentRepository, EnrolmentMapper enrolmentMapper) {
        this.enrolmentRepository = enrolmentRepository;
        this.enrolmentMapper = enrolmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolmentResponse> getAllEnrolments() {
        return enrolmentRepository.findAll().stream()
                .map(enrolmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrolmentResponse getEnrolmentById(Long enrolmentId) {
        Enrolment enrolment = enrolmentRepository.findById(enrolmentId)
                .orElseThrow(() -> new EnrolmentNotFoundException(enrolmentId));

        return enrolmentMapper.toDto(enrolment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolmentResponse> findByStudentId(Long studentId) {
        return enrolmentRepository.findByStudentId(studentId).stream()
                .map(enrolmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolmentResponse> findByCourseId(Long courseId) {
        return enrolmentRepository.findByCourseId(courseId).stream()
                .map(enrolmentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrolmentResponse> searchEnrolments(Long studentId, Long courseId) {
        List<Enrolment> enrolments;
        if (studentId != null && courseId != null) {
            enrolments = enrolmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        } else if (studentId != null) {
            enrolments = enrolmentRepository.findByStudentId(studentId);
        } else if (courseId != null) {
            enrolments = enrolmentRepository.findByCourseId(courseId);
        } else {
            enrolments = enrolmentRepository.findAll();
        }

        return enrolments.stream()
                .map(enrolmentMapper::toDto)
                .toList();
    }
}
