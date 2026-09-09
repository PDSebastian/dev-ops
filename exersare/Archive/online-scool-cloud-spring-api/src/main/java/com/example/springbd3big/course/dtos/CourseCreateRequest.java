package com.example.springbd3big.course.dtos;

import jakarta.validation.constraints.NotBlank;

public record CourseCreateRequest(

     @NotBlank
     String courseName,

     @NotBlank
     String departament

){}
