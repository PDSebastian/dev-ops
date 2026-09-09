package com.example.springbd3big.course.mappers;

import com.example.springbd3big.course.dtos.CourseCreateRequest;
import com.example.springbd3big.course.dtos.CourseResponse;
import com.example.springbd3big.course.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public Course toEntity(CourseCreateRequest req){
        if (req == null) return null;

        return Course.builder()
                .courseName(req.courseName())
                .departament(req.departament())
                .build();
    }

    public CourseResponse toDto(Course course){

        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getDepartament()
        );
    }

}
