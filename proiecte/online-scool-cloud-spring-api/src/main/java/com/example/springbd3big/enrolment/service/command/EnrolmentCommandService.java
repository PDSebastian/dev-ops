package com.example.springbd3big.enrolment.service.command;

import com.example.springbd3big.enrolment.dtos.EnrolmentCreateRequest;
import com.example.springbd3big.enrolment.dtos.EnrolmentResponse;
import com.example.springbd3big.enrolment.dtos.EnrolmentUpdateRequest;

public interface EnrolmentCommandService {

        EnrolmentResponse enrollStudent(EnrolmentCreateRequest req);
        EnrolmentResponse updateEnrolment(Long enrolmentId, EnrolmentUpdateRequest req);
        EnrolmentResponse deleteEnrolment(Long enrolmentId);
}
