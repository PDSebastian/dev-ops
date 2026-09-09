package com.example.springbd3big.enrolment.model;

import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.user.student.model.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "enrolment")
public class Enrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "student_id"
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "course_id"
    )
    private Course course;

    @Override
    public String toString(){
        return "Id: " + id + " Student: " + student + " Course: " + course;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Enrolment enrolment = (Enrolment) o;
        return Objects.equals(id, enrolment.id) && Objects.equals(student, enrolment.student) && Objects.equals(course, enrolment.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, course);
    }
}
