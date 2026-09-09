package com.example.springbd3big.enrolment.service.command.impl;

import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import com.example.springbd3big.enrolment.exceptions.EnrolmentAlreadyExistsException;
import com.example.springbd3big.enrolment.exceptions.EnrolmentNotFoundException;
import com.example.springbd3big.enrolment.mappers.EnrolmentMapper;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.command.EnrolmentCommandService;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnrolmentCommandServiceImpl implements EnrolmentCommandService {

    private final EnrolmentRepository enrolmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrolmentMapper enrolmentMapper;

    public EnrolmentCommandServiceImpl(
            EnrolmentRepository enrolmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrolmentMapper enrolmentMapper
    ){
        this.enrolmentRepository = enrolmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrolmentMapper = enrolmentMapper;

    }


    @Override
    @Transactional
    public EnrolmentResponse enrollStudent(EnrolmentCreateRequest req) {
        if (req == null) {
            throw new RequestBodyMissingException();
        }

        Course course = courseRepository.findById(req.courseId())
                .orElseThrow(()-> new CourseNotFoundException(req.courseId()));
        Student student  = studentRepository.findById(req.studentId())
                .orElseThrow(()-> new StudentNotFoundException(req.studentId()));

        boolean alreadyExists = !enrolmentRepository
                .findByStudentIdAndCourseId(req.studentId(), req.courseId())
                .isEmpty();
        if (alreadyExists) {
            throw new EnrolmentAlreadyExistsException(req.studentId(), req.courseId());
        }

        Enrolment enrolment = Enrolment.builder()
                .student(student)
                .course(course)
                .build();
        enrolmentRepository.save(enrolment);
        return enrolmentMapper.toDto(enrolment);
    }

    @Override
    @Transactional
    public EnrolmentResponse updateEnrolment(Long enrolmentId, EnrolmentUpdateRequest req) {
        Enrolment enrolment = enrolmentRepository.findById(enrolmentId)
                .orElseThrow(() -> new EnrolmentNotFoundException(enrolmentId));

        if (req == null) {
            throw new RequestBodyMissingException();
        }
        if (req.studentId() == null && req.courseId() == null) {
            throw new EmptyUpdateRequestException();
        }

        if (req.studentId() != null) {
            Student student = studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new StudentNotFoundException(req.studentId()));
            enrolment.setStudent(student);
        }
        if (req.courseId() != null) {
            Course course = courseRepository.findById(req.courseId())
                    .orElseThrow(() -> new CourseNotFoundException(req.courseId()));
            enrolment.setCourse(course);
        }

        return enrolmentMapper.toDto(enrolment);
    }

    @Override
    @Transactional
    public EnrolmentResponse deleteEnrolment(Long enrolmentId) {
        Enrolment enrolment = enrolmentRepository.findById(enrolmentId)
                .orElseThrow(() -> new EnrolmentNotFoundException(enrolmentId));

        enrolmentRepository.delete(enrolment);
        return enrolmentMapper.toDto(enrolment);
    }
}
