package com.example.springbd3big.integration;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.query.StudentQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StudentQueryServiceIT {

    @Autowired
    private StudentQueryService studentQueryService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrolmentRepository enrolmentRepository;

    @Test
    @Transactional
    void getAllStudents_returnsDtos() {
        studentRepository.save(Student.builder()
                .firstName("Radu")
                .lastName("Marinescu")
                .email("radu.marinescu@test.com")
                .build());
        studentRepository.save(Student.builder()
                .firstName("Elena")
                .lastName("Dumitrescu")
                .email("elena.dumitrescu@test.com")
                .build());

        List<StudentResponse> students = studentQueryService.getAllStudents();

        assertEquals(2, students.size());
        assertTrue(students.stream().anyMatch(s -> s.email().equals("radu.marinescu@test.com")));
        assertTrue(students.stream().anyMatch(s -> s.email().equals("elena.dumitrescu@test.com")));
    }

    @Test
    @Transactional
    void getStudentById_returnsDto() {
        Student saved = studentRepository.save(Student.builder()
                .firstName("Ioana")
                .lastName("Stan")
                .email("ioana.stan@test.com")
                .build());

        StudentResponse response = studentQueryService.getStudentById((long) saved.getId());

        assertEquals(saved.getId(), response.id());
        assertEquals("Ioana", response.firstName());
        assertEquals("Stan", response.lastName());
        assertEquals("ioana.stan@test.com", response.email());
    }

    @Test
    @Transactional
    void getCoursesForStudent_returnsOnlyEnrolledCourses() {
        Student targetStudent = studentRepository.save(Student.builder()
                .firstName("Teo")
                .lastName("Mihai")
                .email("teo.mihai@test.com")
                .build());
        Student otherStudent = studentRepository.save(Student.builder()
                .firstName("Mara")
                .lastName("Pop")
                .email("mara.pop@test.com")
                .build());

        Course math = courseRepository.save(Course.builder()
                .courseName("Math")
                .departament("Science")
                .build());
        Course history = courseRepository.save(Course.builder()
                .courseName("History")
                .departament("Humanities")
                .build());
        Course music = courseRepository.save(Course.builder()
                .courseName("Music")
                .departament("Arts")
                .build());

        enrolmentRepository.save(Enrolment.builder()
                .student(targetStudent)
                .course(math)
                .build());
        enrolmentRepository.save(Enrolment.builder()
                .student(targetStudent)
                .course(history)
                .build());
        enrolmentRepository.save(Enrolment.builder()
                .student(otherStudent)
                .course(music)
                .build());

        List<CourseResponse> courses = studentQueryService.getCoursesForStudent((long) targetStudent.getId());

        assertEquals(2, courses.size());
        assertTrue(courses.stream().anyMatch(c -> c.courseName().equals("Math")));
        assertTrue(courses.stream().anyMatch(c -> c.courseName().equals("History")));
        assertFalse(courses.stream().anyMatch(c -> c.courseName().equals("Music")));
    }
}
