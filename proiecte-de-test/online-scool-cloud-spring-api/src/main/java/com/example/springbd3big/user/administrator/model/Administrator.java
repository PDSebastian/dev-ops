package com.example.springbd3big.user.administrator.model;

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
@Table(name = "administrator")
public class Administrator extends User {

    @Column(name = "employee_code", length = 50, unique = true)
    private String employeeCode;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "job_title", length = 100)
    private String jobTitle;
}
