package com.example.springbd3big.enrolment.repository;

import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.enrolment.model.Enrolment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {

    @Override
    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrolment> findAll();

    @Override
    @EntityGraph(attributePaths = {"student", "course"})
    Optional<Enrolment> findById(Long enrolmentId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrolment> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrolment> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrolment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("select distinct e.course from Enrolment e where e.student.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);
}
