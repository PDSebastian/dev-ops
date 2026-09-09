package com.example.springbd3big.unit.service.command;

import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;
import com.example.springbd3big.enrolment.exceptions.EnrolmentAlreadyExistsException;
import com.example.springbd3big.enrolment.exceptions.EnrolmentNotFoundException;
import com.example.springbd3big.enrolment.mappers.EnrolmentMapper;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.command.impl.EnrolmentCommandServiceImpl;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrolmentCommandServiceImplTest {

    @Test
    void enrollStudent_throwsWhenBodyMissing() {
        EnrolmentCommandServiceImpl service = new EnrolmentCommandServiceImpl(
                stub(EnrolmentRepository.class, Map.of()),
                stub(StudentRepository.class, Map.of()),
                stub(CourseRepository.class, Map.of()),
                new EnrolmentMapper()
        );

        assertThrows(RequestBodyMissingException.class, () -> service.enrollStudent(null));
    }

    @Test
    void enrollStudent_throwsWhenCourseMissing() {
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                CourseNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(
                        stub(EnrolmentRepository.class, Map.of()),
                        stub(StudentRepository.class, Map.of()),
                        courseRepository,
                        new EnrolmentMapper()
                ).enrollStudent(new EnrolmentCreateRequest(1L, 2L))
        );
    }

    @Test
    void enrollStudent_throwsWhenStudentMissing() {
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(
                        stub(EnrolmentRepository.class, Map.of()),
                        studentRepository,
                        courseRepository,
                        new EnrolmentMapper()
                ).enrollStudent(new EnrolmentCreateRequest(1L, 2L))
        );
    }

    @Test
    void enrollStudent_throwsWhenDuplicateExists() {
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(1);
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findByStudentIdAndCourseId", args -> List.of(Enrolment.builder().student(student).course(course).build())
        ));

        assertThrows(
                EnrolmentAlreadyExistsException.class,
                () -> new EnrolmentCommandServiceImpl(
                        enrolmentRepository,
                        stub(StudentRepository.class, Map.of("findById", args -> Optional.of(student))),
                        stub(CourseRepository.class, Map.of("findById", args -> Optional.of(course))),
                        new EnrolmentMapper()
                ).enrollStudent(new EnrolmentCreateRequest(1L, 2L))
        );
    }

    @Test
    void enrollStudent_savesEnrolmentAndReturnsDto() {
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(1);
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        AtomicReference<Enrolment> savedEnrolment = new AtomicReference<>();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findByStudentIdAndCourseId", args -> List.of(),
                "save", args -> {
                    savedEnrolment.set((Enrolment) args[0]);
                    return args[0];
                }
        ));

        EnrolmentResponse response = new EnrolmentCommandServiceImpl(
                enrolmentRepository,
                stub(StudentRepository.class, Map.of("findById", args -> Optional.of(student))),
                stub(CourseRepository.class, Map.of("findById", args -> Optional.of(course))),
                new EnrolmentMapper()
        ).enrollStudent(new EnrolmentCreateRequest(1L, 2L));

        assertSame(student, savedEnrolment.get().getStudent());
        assertSame(course, savedEnrolment.get().getCourse());
        assertEquals(1, response.studentId());
        assertEquals(2, response.courseId());
    }

    @Test
    void updateEnrolment_throwsWhenMissing() {
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                EnrolmentNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(
                        enrolmentRepository,
                        stub(StudentRepository.class, Map.of()),
                        stub(CourseRepository.class, Map.of()),
                        new EnrolmentMapper()
                ).updateEnrolment(1L, new EnrolmentUpdateRequest(1L, 2L))
        );
    }

    @Test
    void updateEnrolment_throwsWhenBodyMissing() {
        Enrolment enrolment = Enrolment.builder().build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));

        assertThrows(
                RequestBodyMissingException.class,
                () -> new EnrolmentCommandServiceImpl(
                        enrolmentRepository,
                        stub(StudentRepository.class, Map.of()),
                        stub(CourseRepository.class, Map.of()),
                        new EnrolmentMapper()
                ).updateEnrolment(1L, null)
        );
    }

    @Test
    void updateEnrolment_throwsWhenUpdateIsEmpty() {
        Enrolment enrolment = Enrolment.builder().build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));

        assertThrows(
                EmptyUpdateRequestException.class,
                () -> new EnrolmentCommandServiceImpl(
                        enrolmentRepository,
                        stub(StudentRepository.class, Map.of()),
                        stub(CourseRepository.class, Map.of()),
                        new EnrolmentMapper()
                ).updateEnrolment(1L, new EnrolmentUpdateRequest(null, null))
        );
    }

    @Test
    void updateEnrolment_throwsWhenStudentMissing() {
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        Student existingStudent = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        existingStudent.setId(1);
        Enrolment enrolment = Enrolment.builder().student(existingStudent).course(course).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(enrolmentRepository, studentRepository, stub(CourseRepository.class, Map.of()), new EnrolmentMapper())
                        .updateEnrolment(1L, new EnrolmentUpdateRequest(9L, null))
        );
    }

    @Test
    void updateEnrolment_throwsWhenCourseMissing() {
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(1);
        Enrolment enrolment = Enrolment.builder().student(student).course(course).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                CourseNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(enrolmentRepository, stub(StudentRepository.class, Map.of()), courseRepository, new EnrolmentMapper())
                        .updateEnrolment(1L, new EnrolmentUpdateRequest(null, 8L))
        );
    }

    @Test
    void updateEnrolment_updatesBothRelations() {
        Student oldStudent = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        oldStudent.setId(1);
        Student newStudent = Student.builder().firstName("Ana").lastName("Pop").email("ana@test.com").build();
        newStudent.setId(9);
        Course oldCourse = Course.builder().courseName("Java").departament("CS").build();
        oldCourse.setId(2);
        Course newCourse = Course.builder().courseName("DB").departament("IT").build();
        newCourse.setId(8);
        Enrolment enrolment = Enrolment.builder().student(oldStudent).course(oldCourse).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(newStudent)
        ));
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(newCourse)
        ));

        EnrolmentResponse response = new EnrolmentCommandServiceImpl(
                enrolmentRepository,
                studentRepository,
                courseRepository,
                new EnrolmentMapper()
        ).updateEnrolment(1L, new EnrolmentUpdateRequest(9L, 8L));

        assertSame(newStudent, enrolment.getStudent());
        assertSame(newCourse, enrolment.getCourse());
        assertEquals(9, response.studentId());
        assertEquals(8, response.courseId());
    }

    @Test
    void updateEnrolment_updatesOnlyCourseWhenStudentIdIsNull() {
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(1);
        Course oldCourse = Course.builder().courseName("Java").departament("CS").build();
        oldCourse.setId(2);
        Course newCourse = Course.builder().courseName("DB").departament("IT").build();
        newCourse.setId(10);
        Enrolment enrolment = Enrolment.builder().student(student).course(oldCourse).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(newCourse)
        ));

        EnrolmentResponse response = new EnrolmentCommandServiceImpl(
                enrolmentRepository,
                stub(StudentRepository.class, Map.of()),
                courseRepository,
                new EnrolmentMapper()
        ).updateEnrolment(1L, new EnrolmentUpdateRequest(null, 10L));

        assertSame(student, enrolment.getStudent());
        assertSame(newCourse, enrolment.getCourse());
        assertEquals(10, response.courseId());
    }

    @Test
    void updateEnrolment_updatesOnlyStudentWhenCourseIdIsNull() {
        Student oldStudent = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        oldStudent.setId(1);
        Student newStudent = Student.builder().firstName("Ana").lastName("Pop").email("ana@test.com").build();
        newStudent.setId(11);
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(2);
        Enrolment enrolment = Enrolment.builder().student(oldStudent).course(course).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment)
        ));
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(newStudent)
        ));

        EnrolmentResponse response = new EnrolmentCommandServiceImpl(
                enrolmentRepository,
                studentRepository,
                stub(CourseRepository.class, Map.of()),
                new EnrolmentMapper()
        ).updateEnrolment(1L, new EnrolmentUpdateRequest(11L, null));

        assertSame(newStudent, enrolment.getStudent());
        assertSame(course, enrolment.getCourse());
        assertEquals(11, response.studentId());
    }

    @Test
    void deleteEnrolment_throwsWhenMissing() {
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                EnrolmentNotFoundException.class,
                () -> new EnrolmentCommandServiceImpl(
                        enrolmentRepository,
                        stub(StudentRepository.class, Map.of()),
                        stub(CourseRepository.class, Map.of()),
                        new EnrolmentMapper()
                ).deleteEnrolment(4L)
        );
    }

    @Test
    void deleteEnrolment_deletesAndReturnsDto() {
        AtomicReference<Enrolment> deletedEnrolment = new AtomicReference<>();
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(5);
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(6);
        Enrolment enrolment = Enrolment.builder().student(student).course(course).build();
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment),
                "delete", args -> {
                    deletedEnrolment.set((Enrolment) args[0]);
                    return null;
                }
        ));

        EnrolmentResponse response = new EnrolmentCommandServiceImpl(
                enrolmentRepository,
                stub(StudentRepository.class, Map.of()),
                stub(CourseRepository.class, Map.of()),
                new EnrolmentMapper()
        ).deleteEnrolment(4L);

        assertSame(enrolment, deletedEnrolment.get());
        assertEquals(5, response.studentId());
        assertEquals(6, response.courseId());
    }
}
