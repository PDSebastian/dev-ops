package com.example.springbd3big.unit.service.command;

import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.dtos.CourseUpdateRequest;
import com.example.springbd3big.course.exceptions.CourseNotFoundException;
import com.example.springbd3big.course.mappers.CourseMapper;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.course.service.command.impl.CourseCommandServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseCommandServiceImplTest {

    @Test
    void createCourse_mapsAndSavesEntity() {
        AtomicReference<Course> savedCourse = new AtomicReference<>();
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "save", args -> {
                    Course course = (Course) args[0];
                    savedCourse.set(course);
                    course.setId(1);
                    return course;
                }
        ));

        CourseResponse response = new CourseCommandServiceImpl(courseRepository, new CourseMapper())
                .createCourse(new CourseCreateRequest("Java", "CS"));

        assertEquals("Java", savedCourse.get().getCourseName());
        assertEquals("CS", savedCourse.get().getDepartament());
        assertEquals(1, response.id());
    }

    @Test
    void updateCourse_throwsWhenMissing() {
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                CourseNotFoundException.class,
                () -> new CourseCommandServiceImpl(courseRepository, new CourseMapper()).updateCourse(2L, new CourseUpdateRequest("x", "y"))
        );
    }

    @Test
    void updateCourse_throwsWhenBodyMissing() {
        Course course = Course.builder().courseName("Old").departament("Old").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));

        assertThrows(
                RequestBodyMissingException.class,
                () -> new CourseCommandServiceImpl(courseRepository, new CourseMapper()).updateCourse(2L, null)
        );
    }

    @Test
    void updateCourse_throwsWhenUpdateIsEmpty() {
        Course course = Course.builder().courseName("Old").departament("Old").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));

        assertThrows(
                EmptyUpdateRequestException.class,
                () -> new CourseCommandServiceImpl(courseRepository, new CourseMapper()).updateCourse(2L, new CourseUpdateRequest(null, null))
        );
    }

    @Test
    void updateCourse_updatesCourseName() {
        Course course = Course.builder().courseName("Old").departament("Dept").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));

        CourseResponse response = new CourseCommandServiceImpl(courseRepository, new CourseMapper())
                .updateCourse(2L, new CourseUpdateRequest("New", null));

        assertEquals("New", course.getCourseName());
        assertEquals("New", response.courseName());
    }

    @Test
    void updateCourse_updatesDepartment() {
        Course course = Course.builder().courseName("Old").departament("Dept").build();
        course.setId(2);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course)
        ));

        CourseResponse response = new CourseCommandServiceImpl(courseRepository, new CourseMapper())
                .updateCourse(2L, new CourseUpdateRequest(null, "NewDept"));

        assertEquals("NewDept", course.getDepartament());
        assertEquals("NewDept", response.departament());
    }

    @Test
    void deleteCourse_throwsWhenMissing() {
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(CourseNotFoundException.class, () -> new CourseCommandServiceImpl(courseRepository, new CourseMapper()).deleteCourse(3L));
    }

    @Test
    void deleteCourse_deletesAndReturnsDto() {
        AtomicReference<Course> deletedCourse = new AtomicReference<>();
        Course course = Course.builder().courseName("Delete").departament("CS").build();
        course.setId(3);
        CourseRepository courseRepository = stub(CourseRepository.class, Map.of(
                "findById", args -> Optional.of(course),
                "delete", args -> {
                    deletedCourse.set((Course) args[0]);
                    return null;
                }
        ));

        CourseResponse response = new CourseCommandServiceImpl(courseRepository, new CourseMapper()).deleteCourse(3L);

        assertSame(course, deletedCourse.get());
        assertEquals(3, response.id());
    }
}
