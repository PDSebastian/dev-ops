package com.example.springbd3big.user.teacher.model;

import com.example.springbd3big.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "teacher")
public class Teacher extends User {

    @Column(name = "employee_code", length = 50, unique = true)
    private String employeeCode;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "bio", length = 1000)
    private String bio;
}
