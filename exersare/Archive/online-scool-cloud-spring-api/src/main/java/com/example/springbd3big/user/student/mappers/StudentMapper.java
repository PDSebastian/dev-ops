package com.example.springbd3big.user.student.mappers;

import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentCreateRequest req){
        if(req == null) return null;

        return Student.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .build();
    }

    public StudentResponse toDto(Student student){

        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }
}
