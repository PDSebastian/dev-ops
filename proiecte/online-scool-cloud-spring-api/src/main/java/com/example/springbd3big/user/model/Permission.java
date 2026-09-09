package com.example.springbd3big.user.model;

public enum Permission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    TEACHER_READ("teacher:read"),
    TEACHER_WRITE("teacher:write"),
    ADMINISTRATOR_READ("administrator:read"),
    ADMINISTRATOR_WRITE("administrator:write"),
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write"),
    BOOK_READ("book:read"),
    BOOK_WRITE("book:write"),
    ENROLMENT_READ("enrolment:read"),
    ENROLMENT_WRITE("enrolment:write"),
    PROFILE_READ("profile:read"),
    PROFILE_WRITE("profile:write"),
    AUTH_READ("auth:read"),
    AUTH_WRITE("auth:write"),
    PASSWORD_READ("password:read"),
    PASSWORD_WRITE("password:write"),
    PERMISSION_READ("permission:read"),
    PERMISSION_WRITE("permission:write"),
    DASHBOARD_READ("dashboard:read"),
    DASHBOARD_WRITE("dashboard:write"),
    REPORT_READ("report:read"),
    REPORT_WRITE("report:write"),
    SETTINGS_READ("settings:read"),
    SETTINGS_WRITE("settings:write");

    private final String authority;

    Permission(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
