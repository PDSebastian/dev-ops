package com.example.springbd3big.user.student.model;

import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student extends User {

    @OneToMany(mappedBy = "student",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @Builder.Default
    Set<Book> books = new HashSet<>();

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    Set<Enrolment> enrolments = new HashSet<>();

    @Column(name = "student_code", length = 50, unique = true)
    private String studentCode;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "guardian_name", length = 100)
    private String guardianName;

    @Column(name = "guardian_email", length = 100)
    private String guardianEmail;

    //BOOKS
    public void addBook(Book book){
        this.books.add(book);
        book.setStudent(this);
    }

    public void deleteBook(Book book){
        this.books.remove(book);
        book.setStudent(null);
    }

    //ENROLMENTS
    public void addEnrolment(Enrolment enrolment, Course course){
        this.enrolments.add(enrolment);
        enrolment.setStudent(this);
        enrolment.setCourse(course);
    }

    public void deleteEnrolments(Enrolment enrolment){
        this.enrolments.remove(enrolment);
        enrolment.setStudent(null);
        enrolment.setCourse(null);
    }







}
