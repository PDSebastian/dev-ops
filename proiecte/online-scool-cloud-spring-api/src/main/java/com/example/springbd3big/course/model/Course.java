package com.example.springbd3big.course.model;

import com.example.springbd3big.enrolment.model.Enrolment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "course_name")
    @NotBlank
    private String courseName;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @EqualsAndHashCode.Exclude
    private Set<Enrolment> enrolments = new HashSet<>();

    @Column(name = "department")
    @NotBlank
    private String departament;

    @Override
    public String toString(){
        return "Id: " + id + "CourseName: " + courseName + "Department: " + departament;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && Objects.equals(courseName, course.courseName) && Objects.equals(enrolments, course.enrolments) && Objects.equals(departament, course.departament);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseName, enrolments, departament);
    }
}
