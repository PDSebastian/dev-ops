package com.example.springbd3big.user.model;

import java.util.EnumSet;
import java.util.Set;

public final class PermissionTemplates {

    private static final Set<Permission> STUDENT_DEFAULTS = EnumSet.of(
            Permission.STUDENT_READ,
            Permission.COURSE_READ,
            Permission.BOOK_READ,
            Permission.BOOK_WRITE,
            Permission.ENROLMENT_READ,
            Permission.PROFILE_READ,
            Permission.PROFILE_WRITE,
            Permission.AUTH_READ,
            Permission.AUTH_WRITE,
            Permission.PASSWORD_READ,
            Permission.PASSWORD_WRITE,
            Permission.DASHBOARD_READ
    );

    private static final Set<Permission> TEACHER_DEFAULTS = EnumSet.of(
            Permission.TEACHER_READ,
            Permission.TEACHER_WRITE,
            Permission.STUDENT_READ,
            Permission.COURSE_READ,
            Permission.COURSE_WRITE,
            Permission.BOOK_READ,
            Permission.ENROLMENT_READ,
            Permission.ENROLMENT_WRITE,
            Permission.PROFILE_READ,
            Permission.PROFILE_WRITE,
            Permission.AUTH_READ,
            Permission.AUTH_WRITE,
            Permission.PASSWORD_READ,
            Permission.PASSWORD_WRITE,
            Permission.DASHBOARD_READ,
            Permission.DASHBOARD_WRITE,
            Permission.REPORT_READ
    );

    private static final Set<Permission> ADMINISTRATOR_DEFAULTS = EnumSet.of(
            Permission.STUDENT_READ,
            Permission.STUDENT_WRITE,
            Permission.TEACHER_READ,
            Permission.TEACHER_WRITE,
            Permission.ADMINISTRATOR_READ,
            Permission.ADMINISTRATOR_WRITE,
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.COURSE_READ,
            Permission.COURSE_WRITE,
            Permission.BOOK_READ,
            Permission.BOOK_WRITE,
            Permission.ENROLMENT_READ,
            Permission.ENROLMENT_WRITE,
            Permission.PROFILE_READ,
            Permission.PROFILE_WRITE,
            Permission.AUTH_READ,
            Permission.AUTH_WRITE,
            Permission.PASSWORD_READ,
            Permission.PASSWORD_WRITE,
            Permission.PERMISSION_READ,
            Permission.PERMISSION_WRITE,
            Permission.DASHBOARD_READ,
            Permission.DASHBOARD_WRITE,
            Permission.REPORT_READ,
            Permission.REPORT_WRITE,
            Permission.SETTINGS_READ,
            Permission.SETTINGS_WRITE
    );

    private PermissionTemplates() {
    }

    public static Set<Permission> studentDefaults() {
        return EnumSet.copyOf(STUDENT_DEFAULTS);
    }

    public static Set<Permission> teacherDefaults() {
        return EnumSet.copyOf(TEACHER_DEFAULTS);
    }

    public static Set<Permission> administratorDefaults() {
        return EnumSet.copyOf(ADMINISTRATOR_DEFAULTS);
    }
}
