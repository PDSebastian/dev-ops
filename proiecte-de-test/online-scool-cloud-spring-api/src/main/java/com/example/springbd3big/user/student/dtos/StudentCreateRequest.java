package com.example.springbd3big.user.student.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record StudentCreateRequest(

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String firstName,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String lastName,

        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String email

){}
