package com.example.springbd3big.unit.service.query;

import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.query.impl.CourseQueryServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseQueryServiceImplTest {

    @Test
    void getAllCourses_mapsAllResults() {
        Course course = Course.builder().courseName("Java").departament("CS").build();
        course.setId(1);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findAll", args -> List.of(course)
        ));

        List<CourseResponse> responses = new CourseQueryServiceImpl(courseRepository, new CourseMapper()).getAllCourses();

        assertEquals(1, responses.size());
        assertEquals("Java", responses.getFirst().courseName());
    }

    @Test
    void getCourseById_throwsWhenMissing() {
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(CourseNotFoundException.class, () -> new CourseQueryServiceImpl(courseRepository, new CourseMapper()).getCourseById(1L));
    }

    @Test
    void getCourseById_mapsResult() {
        Course course = Course.builder().courseName("OS").departament("CS").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));

        CourseResponse response = new CourseQueryServiceImpl(courseRepository, new CourseMapper()).getCourseById(2L);

        assertEquals(2, response.id());
        assertEquals("OS", response.courseName());
    }

    @Test
    void findByDepartment_mapsRepositoryResults() {
        Course course = Course.builder().courseName("Networks").departament("CS").build();
        course.setId(3);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findByDepartamentContainingIgnoreCase", args -> List.of(course)
        ));

        List<CourseResponse> responses = new CourseQueryServiceImpl(courseRepository, new CourseMapper()).findByDepartment("CS");

        assertEquals(1, responses.size());
        assertEquals("Networks", responses.getFirst().courseName());
    }

    @Test
    void searchCourses_returnsEmptyForNullQuery() {
        AtomicReference<String> calledWith = new AtomicReference<>("not-called");
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findByCourseNameContainingIgnoreCaseOrDepartamentContainingIgnoreCase", args -> {
                    calledWith.set((String) args[0]);
                    return List.of();
                }
        ));

        List<CourseResponse> responses = new CourseQueryServiceImpl(courseRepository, new CourseMapper()).searchCourses(null);

        assertTrue(responses.isEmpty());
        assertEquals("not-called", calledWith.get());
    }

    @Test
    void searchCourses_returnsEmptyForBlankQuery() {
        List<CourseResponse> responses = new CourseQueryServiceImpl(
                stub(CourseRepository.class, Map.of()),
                new CourseMapper()
        ).searchCourses("   ");

        assertTrue(responses.isEmpty());
    }

    @Test
    void searchCourses_trimsAndSearches() {
        AtomicReference<String> firstArg = new AtomicReference<>();
        AtomicReference<String> secondArg = new AtomicReference<>();
        Course course = Course.builder().courseName("Databases").departament("IT").build();
        course.setId(4);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findByCourseNameContainingIgnoreCaseOrDepartamentContainingIgnoreCase", args -> {
                    firstArg.set((String) args[0]);
                    secondArg.set((String) args[1]);
                    return List.of(course);
                }
        ));

        List<CourseResponse> responses = new CourseQueryServiceImpl(courseRepository, new CourseMapper()).searchCourses("  data  ");

        assertEquals("data", firstArg.get());
        assertEquals("data", secondArg.get());
        assertEquals(1, responses.size());
    }
}
