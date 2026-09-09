package com.example.springbd3big.user.teacher.mappers;

import com.example.springbd3big.user.teacher.dtos.TeacherResponse;
import com.example.springbd3big.user.teacher.model.Teacher;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

    public TeacherResponse toDto(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getEmail(),
                teacher.getEmployeeCode(),
                teacher.getSpecialization(),
                teacher.getTitle()
        );
    }
}
