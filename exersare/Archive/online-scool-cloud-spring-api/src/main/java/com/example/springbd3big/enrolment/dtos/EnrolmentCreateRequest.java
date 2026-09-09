package com.example.springbd3big.enrolment.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EnrolmentCreateRequest(

        @NotNull
        Long studentId,

        @NotNull
        Long courseId

){}
