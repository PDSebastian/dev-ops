package com.example.springbd3big.integration;

import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.query.EnrolmentQueryService;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EnrolmentQueryServiceIT {

    @Autowired
    private EnrolmentQueryService enrolmentQueryService;

    @Autowired
    private EnrolmentRepository enrolmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Transactional
    void getAllEnrolments_returnsDtos() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Oana")
                .lastName("Barbu")
                .email("oana.barbu@test.com")
                .build());
        Course course1 = courseRepository.save(Course.builder()
                .courseName("Music")
                .departament("Arts")
                .build());
        Course course2 = courseRepository.save(Course.builder()
                .courseName("Geography")
                .departament("Science")
                .build());

        enrolmentRepository.save(Enrolment.builder()
                .student(student)
                .course(course1)
                .build());
        enrolmentRepository.save(Enrolment.builder()
                .student(student)
                .course(course2)
                .build());

        List<EnrolmentResponse> enrolments = enrolmentQueryService.getAllEnrolments();

        assertEquals(2, enrolments.size());
    }

    @Test
    @Transactional
    void getEnrolmentById_returnsDto() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Roxana")
                .lastName("Filip")
                .email("roxana.filip@test.com")
                .build());
        Course course = courseRepository.save(Course.builder()
                .courseName("Philosophy")
                .departament("Humanities")
                .build());

        Enrolment enrolment = enrolmentRepository.save(Enrolment.builder()
                .student(student)
                .course(course)
                .build());

        EnrolmentResponse response = enrolmentQueryService.getEnrolmentById(enrolment.getId());

        assertEquals(student.getId(), response.studentId());
        assertEquals(course.getId(), response.courseId());
    }
}
