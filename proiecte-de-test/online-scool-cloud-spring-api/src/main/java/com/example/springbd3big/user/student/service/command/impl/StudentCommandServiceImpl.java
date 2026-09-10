package com.example.springbd3big.user.student.service.command.impl;

import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.user.student.exceptions.StudentAlreadyExistsException;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.mappers.StudentMapper;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.command.StudentCommandService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    public StudentCommandServiceImpl(StudentMapper studentMapper, StudentRepository studentRepository) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public StudentResponse createStudent(StudentCreateRequest req) {
        if (req == null) {
            throw new RequestBodyMissingException();
        }

        if (req.email() == null || req.email().isBlank()) {
            throw new InvalidRequestException("email must not be blank");
        }

        if (studentRepository.existsByEmailJPQL(req.email().toLowerCase())) {
            throw new StudentAlreadyExistsException(req.email());
        }

        Student savedStudent = studentRepository.save(studentMapper.toEntity(req));

        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long studentId, StudentUpdateRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new StudentNotFoundException(studentId));

        if (req == null) {
            throw new RequestBodyMissingException();
        }
        if (req.firstName() == null && req.lastName() == null && req.email() == null) {
            throw new EmptyUpdateRequestException();
        }

        if (req.firstName() != null) {
            student.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            student.setLastName(req.lastName());
        }
        if (req.email() != null) {
            student.setEmail(req.email());
        }

        return studentMapper.toDto(student);
    }

    @Override
    @Transactional
    public StudentResponse deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new StudentNotFoundException(studentId));

        studentRepository.delete(student);

        return studentMapper.toDto(student);
    }


}
