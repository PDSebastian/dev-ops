package com.example.springbd3big.user.student.service.query.impl;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.mappers.StudentMapper;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.query.StudentQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class StudentQueryServiceImpl implements StudentQueryService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final EnrolmentRepository enrolmentRepository;
    private final CourseMapper courseMapper;

    public StudentQueryServiceImpl(
            StudentRepository studentRepository,
            StudentMapper studentMapper,
            EnrolmentRepository enrolmentRepository,
            CourseMapper courseMapper
    ){
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
        this.enrolmentRepository = enrolmentRepository;
        this.courseMapper = courseMapper;

//        this.getAllStudents();
//        this.getStudentById(1L);
    }
    @Override
    @Transactional
    public List<StudentResponse> getAllStudents() {
        System.out.println("Get All Students:");
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponse getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new StudentNotFoundException(studentId));

        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("email must not be blank");
        }

        Student student = studentRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new StudentNotFoundException(email));

        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        return studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        normalized, normalized, normalized
                )
                .stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<CourseResponse> getCoursesForStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }

        return enrolmentRepository.findCoursesByStudentId(studentId).stream()
                .map(courseMapper::toDto)
                .toList();
    }

}
