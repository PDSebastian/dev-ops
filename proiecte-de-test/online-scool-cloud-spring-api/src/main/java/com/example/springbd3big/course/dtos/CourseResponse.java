package com.example.springbd3big.course.dtos;

import jakarta.validation.constraints.NotBlank;

public record CourseResponse(

        int id,
        String courseName,
        String departament

){}
