package com.example.springbd3big.integration;

import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.command.EnrolmentCommandService;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EnrolmentCommandServiceIT {

    @Autowired
    private EnrolmentCommandService enrolmentCommandService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrolmentRepository enrolmentRepository;

    @Test
    @Transactional
    void enrollStudent_createsEnrolment() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Teodor")
                .lastName("Istrate")
                .email("teodor.istrate@test.com")
                .build());
        Course course = courseRepository.save(Course.builder()
                .courseName("Math")
                .departament("STEM")
                .build());

        EnrolmentCreateRequest req = EnrolmentCreateRequest.builder()
                .studentId((long) student.getId())
                .courseId((long) course.getId())
                .build();

        EnrolmentResponse response = enrolmentCommandService.enrollStudent(req);

        assertNotNull(response);
        assertEquals(student.getId(), response.studentId());
        assertEquals(course.getId(), response.courseId());
        assertEquals(1, enrolmentRepository.count());
    }

    @Test
    @Transactional
    void updateEnrolment_updatesStudentAndCourse() {
        Student student1 = studentRepository.save(Student.builder()
                .firstName("Ana")
                .lastName("Ion")
                .email("ana.ion@test.com")
                .build());
        Student student2 = studentRepository.save(Student.builder()
                .firstName("Mihai")
                .lastName("Pop")
                .email("mihai.pop@test.com")
                .build());
        Course course1 = courseRepository.save(Course.builder()
                .courseName("Biology")
                .departament("Science")
                .build());
        Course course2 = courseRepository.save(Course.builder()
                .courseName("History")
                .departament("Arts")
                .build());

        Enrolment enrolment = enrolmentRepository.save(Enrolment.builder()
                .student(student1)
                .course(course1)
                .build());

        EnrolmentUpdateRequest req = new EnrolmentUpdateRequest(
                (long) student2.getId(),
                (long) course2.getId()
        );

        EnrolmentResponse updated = enrolmentCommandService.updateEnrolment(enrolment.getId(), req);

        assertNotNull(updated);
        assertEquals(student2.getId(), updated.studentId());
        assertEquals(course2.getId(), updated.courseId());
    }

    @Test
    @Transactional
    void deleteEnrolment_removesEntity() {
        Student student = studentRepository.save(Student.builder()
                .firstName("Dana")
                .lastName("Muresan")
                .email("dana.muresan@test.com")
                .build());
        Course course = courseRepository.save(Course.builder()
                .courseName("Chemistry")
                .departament("Science")
                .build());

        Enrolment enrolment = enrolmentRepository.save(Enrolment.builder()
                .student(student)
                .course(course)
                .build());

        Long id = enrolment.getId();
        EnrolmentResponse deleted = enrolmentCommandService.deleteEnrolment(id);

        assertNotNull(deleted);
        assertFalse(enrolmentRepository.findById(id).isPresent());
    }
}
