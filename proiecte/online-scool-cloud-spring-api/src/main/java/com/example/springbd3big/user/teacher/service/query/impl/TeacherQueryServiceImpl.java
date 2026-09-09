package com.example.springbd3big.user.teacher.service.query.impl;

import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.user.teacher.dtos.TeacherResponse;
import com.example.springbd3big.user.teacher.exceptions.TeacherNotFoundException;
import com.example.springbd3big.user.teacher.mappers.TeacherMapper;
import com.example.springbd3big.user.teacher.repository.TeacherRepository;
import com.example.springbd3big.user.teacher.service.query.TeacherQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TeacherQueryServiceImpl implements TeacherQueryService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public TeacherQueryServiceImpl(TeacherRepository teacherRepository, TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .map(teacherMapper::toDto)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("email must not be blank");
        }

        return teacherRepository.findByEmailIgnoreCase(email)
                .map(teacherMapper::toDto)
                .orElseThrow(() -> new TeacherNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> searchTeachers(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        return teacherRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        normalized,
                        normalized,
                        normalized
                )
                .stream()
                .map(teacherMapper::toDto)
                .toList();
    }
}
