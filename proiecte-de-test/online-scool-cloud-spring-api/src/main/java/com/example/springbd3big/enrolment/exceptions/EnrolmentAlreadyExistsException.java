package com.example.springbd3big.enrolment.exceptions;

public class EnrolmentAlreadyExistsException extends RuntimeException {
    public EnrolmentAlreadyExistsException(Long studentId, Long courseId) {
        super("Enrolment already exists for studentId=" + studentId + " and courseId=" + courseId);
    }
}
