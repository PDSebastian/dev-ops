package com.example.springbd3big.unit.service.query;

import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.mappers.StudentMapper;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.query.impl.StudentQueryServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentQueryServiceImplTest {

    @Test
    void getAllStudents_mapsAllResults() {
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(1);
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findAll", args -> List.of(student)
        ));

        List<StudentResponse> responses = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).getAllStudents();

        assertEquals(1, responses.size());
        assertEquals("Ion", responses.getFirst().firstName());
    }

    @Test
    void getStudentById_throwsWhenMissing() {
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new StudentQueryServiceImpl(studentRepository, new StudentMapper(), stub(EnrolmentRepository.class, Map.of()), new CourseMapper())
                        .getStudentById(1L)
        );
    }

    @Test
    void getStudentById_mapsResult() {
        Student student = Student.builder().firstName("Ana").lastName("Pop").email("ana@test.com").build();
        student.setId(2);
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        StudentResponse response = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).getStudentById(2L);

        assertEquals(2, response.id());
        assertEquals("Ana", response.firstName());
    }

    @Test
    void findByEmail_throwsWhenBlank() {
        StudentQueryServiceImpl service = new StudentQueryServiceImpl(
                stub(StudentRepository.class, Map.of()),
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        );

        assertThrows(InvalidRequestException.class, () -> service.findByEmail(" "));
    }

    @Test
    void findByEmail_throwsWhenNull() {
        StudentQueryServiceImpl service = new StudentQueryServiceImpl(
                stub(StudentRepository.class, Map.of()),
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        );

        assertThrows(InvalidRequestException.class, () -> service.findByEmail(null));
    }

    @Test
    void findByEmail_throwsWhenMissing() {
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findByEmailIgnoreCase", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new StudentQueryServiceImpl(studentRepository, new StudentMapper(), stub(EnrolmentRepository.class, Map.of()), new CourseMapper())
                        .findByEmail("missing@test.com")
        );
    }

    @Test
    void findByEmail_returnsMappedStudent() {
        Student student = Student.builder().firstName("Mara").lastName("I").email("mara@test.com").build();
        student.setId(3);
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findByEmailIgnoreCase", args -> Optional.of(student)
        ));

        StudentResponse response = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).findByEmail("mara@test.com");

        assertEquals(3, response.id());
    }

    @Test
    void searchStudents_returnsEmptyForNullQuery() {
        AtomicReference<String> calledWith = new AtomicReference<>("not-called");
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase", args -> {
                    calledWith.set((String) args[0]);
                    return List.of();
                }
        ));

        List<StudentResponse> responses = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).searchStudents(null);

        assertTrue(responses.isEmpty());
        assertEquals("not-called", calledWith.get());
    }

    @Test
    void searchStudents_returnsEmptyForBlankQuery() {
        List<StudentResponse> responses = new StudentQueryServiceImpl(
                stub(StudentRepository.class, Map.of()),
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).searchStudents("  ");

        assertTrue(responses.isEmpty());
    }

    @Test
    void searchStudents_trimsAndSearchesAllFields() {
        AtomicReference<String> firstArg = new AtomicReference<>();
        AtomicReference<String> secondArg = new AtomicReference<>();
        AtomicReference<String> thirdArg = new AtomicReference<>();
        Student student = Student.builder().firstName("Andrei").lastName("Stan").email("andrei@test.com").build();
        student.setId(4);
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase", args -> {
                    firstArg.set((String) args[0]);
                    secondArg.set((String) args[1]);
                    thirdArg.set((String) args[2]);
                    return List.of(student);
                }
        ));

        List<StudentResponse> responses = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                stub(EnrolmentRepository.class, Map.of()),
                new CourseMapper()
        ).searchStudents("  an  ");

        assertEquals("an", firstArg.get());
        assertEquals("an", secondArg.get());
        assertEquals("an", thirdArg.get());
        assertEquals(1, responses.size());
    }

    @Test
    void getCoursesForStudent_throwsWhenStudentMissing() {
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "existsById", args -> false
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new StudentQueryServiceImpl(studentRepository, new StudentMapper(), stub(EnrolmentRepository.class, Map.of()), new CourseMapper())
                        .getCoursesForStudent(5L)
        );
    }

    @Test
    void getCoursesForStudent_mapsCourses() {
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(6);
        StudentRepository studentRepository = stub(StudentRepository.class, Map.of(
                "existsById", args -> true
        ));
        EnrolmentRepository enrolmentRepository = stub(EnrolmentRepository.class, Map.of(
                "findCoursesByStudentId", args -> List.of(course)
        ));

        List<CourseResponse> responses = new StudentQueryServiceImpl(
                studentRepository,
                new StudentMapper(),
                enrolmentRepository,
                new CourseMapper()
        ).getCoursesForStudent(6L);

        assertEquals(1, responses.size());
        assertEquals("Java", responses.getFirst().courseName());
    }
}
