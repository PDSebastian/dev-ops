package com.example.springbd3big.course.repository;

import com.example.springbd3big.course.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Override
    @EntityGraph(attributePaths = {"enrolments"})
    List<Course> findAll();

    @Override
    @EntityGraph
    Optional<Course> findById(Long courseId);

    @EntityGraph(attributePaths = {"enrolments"})
    List<Course> findByDepartamentContainingIgnoreCase(String department);

    @EntityGraph(attributePaths = {"enrolments"})
    List<Course> findByCourseNameContainingIgnoreCaseOrDepartamentContainingIgnoreCase(
            String courseName,
            String departament
    );
}
