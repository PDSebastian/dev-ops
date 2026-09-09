package com.example.springbd3big.unit.service.query;

import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.exceptions.EnrolmentNotFoundException;
import com.example.springbd3big.enrolment.mappers.EnrolmentMapper;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.enrolment.service.query.impl.EnrolmentQueryServiceImpl;
import com.example.springbd3big.user.student.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrolmentQueryServiceImplTest {

    @Test
    void getAllEnrolments_mapsAllResults() {
        Enrolment enrolment = enrolment(1, 2);
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findAll", args -> List.of(enrolment)
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).getAllEnrolments();

        assertEquals(1, responses.size());
        assertEquals(1, responses.getFirst().studentId());
    }

    @Test
    void getEnrolmentById_throwsWhenMissing() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(EnrolmentNotFoundException.class, () -> new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).getEnrolmentById(1L));
    }

    @Test
    void getEnrolmentById_mapsResult() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findById", args -> Optional.of(enrolment(3, 4))
        ));

        EnrolmentResponse response = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).getEnrolmentById(1L);

        assertEquals(3, response.studentId());
        assertEquals(4, response.courseId());
    }

    @Test
    void findByStudentId_mapsResults() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findByStudentId", args -> List.of(enrolment(5, 6))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).findByStudentId(5L);

        assertEquals(1, responses.size());
        assertEquals(6, responses.getFirst().courseId());
    }

    @Test
    void findByCourseId_mapsResults() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findByCourseId", args -> List.of(enrolment(7, 8))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).findByCourseId(8L);

        assertEquals(1, responses.size());
        assertEquals(7, responses.getFirst().studentId());
    }

    @Test
    void searchEnrolments_usesStudentAndCourseWhenBothProvided() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findByStudentIdAndCourseId", args -> List.of(enrolment(1, 2))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).searchEnrolments(1L, 2L);

        assertEquals(1, responses.size());
        assertEquals(2, responses.getFirst().courseId());
    }

    @Test
    void searchEnrolments_usesStudentOnly() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findByStudentId", args -> List.of(enrolment(3, 4))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).searchEnrolments(3L, null);

        assertEquals(1, responses.size());
        assertEquals(3, responses.getFirst().studentId());
    }

    @Test
    void searchEnrolments_usesCourseOnly() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findByCourseId", args -> List.of(enrolment(5, 6))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).searchEnrolments(null, 6L);

        assertEquals(1, responses.size());
        assertEquals(6, responses.getFirst().courseId());
    }

    @Test
    void searchEnrolments_usesFindAllWhenNoFilters() {
        EnrolmentRepository repository = stub(EnrolmentRepository.class, Map.of(
                "findAll", args -> List.of(enrolment(7, 8))
        ));

        List<EnrolmentResponse> responses = new EnrolmentQueryServiceImpl(repository, new EnrolmentMapper()).searchEnrolments(null, null);

        assertEquals(1, responses.size());
        assertEquals(7, responses.getFirst().studentId());
    }

    private Enrolment enrolment(int studentId, int courseId) {
        Student student = Student.builder().firstName("S").lastName("T").email("s@test.com").build();
        student.setId(studentId);
        Course course = Course.builder().courseName("C").departament("D").build();
        course.setId(courseId);
        return Enrolment.builder().student(student).course(course).build();
    }
}
