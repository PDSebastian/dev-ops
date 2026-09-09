package com.example.springbd3big.enrolment.exceptions;

public class EnrolmentNotFoundException extends RuntimeException {
    public EnrolmentNotFoundException(Long enrolmentId) {
        super("Enrolment not found with id=" + enrolmentId);
    }
}
