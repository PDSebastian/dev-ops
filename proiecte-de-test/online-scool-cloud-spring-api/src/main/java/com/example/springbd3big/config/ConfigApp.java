package com.example.springbd3big.config;

import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.service.command.EnrolmentCommandService;
import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.command.StudentCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Configuration
public class ConfigApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigApp.class);

    private final EnrolmentCommandService enrolmentCommandService;
    private final StudentCommandService studentCommandService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ConfigApp(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrolmentCommandService enrolmentCommandService,
            StudentCommandService studentCommandService
    ){
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrolmentCommandService = enrolmentCommandService;
        this.studentCommandService = studentCommandService;
    }

    @Bean
    @Profile("dev")
    ApplicationRunner configAppRunner() {
        return args -> {
            runSafely("testFindAllStudents", this::testFindAllStudents);
            runSafely("testFindStudentById", this::testFindStudentById);
            runSafely("testFindAllCourses", this::testFindAllCourses);
            runSafely("testStudentExistsByEmailJPQL", this::testStudentExistsByEmailJPQL);
            runSafely("testEnrollStudent", this::testEnrollStudent);
            runSafely("testCreateStudent", this::testCreateStudent);
            runSafely("testUpdateStudent", this::testUpdateStudent);
            runSafely("testRemoveStudent", this::testRemoveStudent);
        };
    }

    private void runSafely(String name, Runnable action) {
        try {
            LOGGER.info("Running {}", name);
            action.run();
            LOGGER.info("Finished {}", name);
        } catch (Exception ex) {
            LOGGER.error("Failed {}", name, ex);
        }
    }

    public void testFindAllStudents(){
        studentRepository.findAll().forEach(System.out::println);
    }

    public void testFindStudentById(){
        Optional<Long> studentId = findAnyStudentId();
        if (studentId.isEmpty()) {
            LOGGER.warn("No students found for testFindStudentById");
            return;
        }
        System.out.println(studentRepository.findById(studentId.get()));
    }

    public void testFindAllCourses(){
        courseRepository.findAll().forEach(System.out::println);
    }

    public void testStudentExistsByEmailJPQL(){
        if(studentRepository.existsByEmailJPQL("maria.ionesc@email.com")){
            System.out.println("Exista");
        } else System.out.println("Nu exista");

    }

    public void testEnrollStudent(){
        Optional<Long> studentId = findAnyStudentId();
        Optional<Long> courseId = findAnyCourseId();
        if (studentId.isEmpty() || courseId.isEmpty()) {
            LOGGER.warn("Missing data for testEnrollStudent. studentId={}, courseId={}", studentId.orElse(null), courseId.orElse(null));
            return;
        }
        EnrolmentCreateRequest testReq = EnrolmentCreateRequest.builder()
                .studentId(studentId.get())
                .courseId(courseId.get())
                .build();
        System.out.println("Enrolment salvat");
        enrolmentCommandService.enrollStudent(testReq);
    }

    public void testCreateStudent(){
        StudentCreateRequest createReq = new StudentCreateRequest(
                "createdFirstname",
                "createdLastName",
                "createdEmail@create.com");

        StudentResponse created = studentCommandService.createStudent(createReq);
        System.out.printf("Studentul " + created + " a fost creat");

    }

    public void testUpdateStudent(){
        Optional<Long> studentId = findAnyStudentId();
        if (studentId.isEmpty()) {
            LOGGER.warn("No students found for testUpdateStudent");
            return;
        }
        StudentUpdateRequest updateReq = new StudentUpdateRequest(
                "Vali",
                "Marin",
                "email");

        StudentResponse updated = studentCommandService.updateStudent(studentId.get(), updateReq);
        System.out.println("Studentul " + updated + " a fost updatat");
    }

    public void testRemoveStudent(){
        Optional<Long> studentId = findAnyStudentId();
        if (studentId.isEmpty()) {
            LOGGER.warn("No students found for testRemoveStudent");
            return;
        }
        studentCommandService.deleteStudent(studentId.get());
    }

    private Optional<Long> findAnyStudentId() {
        return studentRepository.findAll(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(student -> (long) student.getId());
    }

    private Optional<Long> findAnyCourseId() {
        return courseRepository.findAll(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(course -> (long) course.getId());
    }



}
