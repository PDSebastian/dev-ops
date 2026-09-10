package com.example.springbd3big.user.student.repository;

import com.example.springbd3big.user.student.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Override
    @EntityGraph(attributePaths = {"books","enrolments"})
    List<Student> findAll();

    @Override
    @EntityGraph(attributePaths = {"books", "enrolments"})
    Optional<Student> findById(Long studentId);

    @EntityGraph(attributePaths = {"books", "enrolments"})
    Optional<Student> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"books", "enrolments"})
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName,
            String lastName,
            String email
    );

    @Query("select (count(s)>0) from Student s where lower(s.email) = :email")
    boolean existsByEmailJPQL(@Param("email") String email);
}
