package com.example.springbd3big.enrolment.mappers;

import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.model.Enrolment;
import org.springframework.stereotype.Component;

@Component
public class EnrolmentMapper {

    public EnrolmentResponse toDto(Enrolment enrolment){

        return new EnrolmentResponse(
                enrolment.getStudent().getId(),
                enrolment.getCourse().getId()
        );
    }
}
