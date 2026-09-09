-- liquibase formatted sql

-- changeset codex:001-initial-schema-and-seed
CREATE TABLE app_user (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30),
    password_hash VARCHAR(255),
    is_active BIT(1) NOT NULL,
    last_login_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_app_user_email UNIQUE (email)
);

CREATE TABLE user_permissions (
    user_id INT NOT NULL,
    permission VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, permission),
    CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE administrator (
    id INT NOT NULL,
    employee_code VARCHAR(50),
    department VARCHAR(100),
    job_title VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT uk_administrator_employee_code UNIQUE (employee_code),
    CONSTRAINT fk_administrator_user FOREIGN KEY (id) REFERENCES app_user (id)
);

CREATE TABLE teacher (
    id INT NOT NULL,
    employee_code VARCHAR(50),
    specialization VARCHAR(100),
    title VARCHAR(100),
    bio VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT uk_teacher_employee_code UNIQUE (employee_code),
    CONSTRAINT fk_teacher_user FOREIGN KEY (id) REFERENCES app_user (id)
);

CREATE TABLE student (
    id INT NOT NULL,
    student_code VARCHAR(50),
    enrollment_date DATE,
    date_of_birth DATE,
    guardian_name VARCHAR(100),
    guardian_email VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT uk_student_student_code UNIQUE (student_code),
    CONSTRAINT fk_student_user FOREIGN KEY (id) REFERENCES app_user (id)
);

CREATE TABLE course (
    id INT NOT NULL AUTO_INCREMENT,
    course_name VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE book (
    id INT NOT NULL AUTO_INCREMENT,
    book_name VARCHAR(50) NOT NULL,
    student_id INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_book_student FOREIGN KEY (student_id) REFERENCES student (id)
);

CREATE TABLE enrolment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id INT,
    course_id INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_enrolment_student FOREIGN KEY (student_id) REFERENCES student (id),
    CONSTRAINT fk_enrolment_course FOREIGN KEY (course_id) REFERENCES course (id)
);

INSERT INTO app_user (
    id,
    first_name,
    last_name,
    email,
    phone_number,
    password_hash,
    is_active,
    last_login_at,
    created_at,
    updated_at
) VALUES
    (1, 'System', 'Administrator', 'admin@online-school.ro', '+40740000001', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (10, 'Andrei', 'Ionescu', 'andrei.ionescu@online-school.ro', '+40740000010', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (11, 'Maria', 'Popescu', 'maria.popescu@online-school.ro', '+40740000011', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (12, 'Alexandru', 'Stan', 'alexandru.stan@online-school.ro', '+40740000012', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (13, 'Ioana', 'Dumitrescu', 'ioana.dumitrescu@online-school.ro', '+40740000013', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (14, 'Radu', 'Georgescu', 'radu.georgescu@online-school.ro', '+40740000014', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (20, 'Ana', 'Marinescu', 'ana.marinescu@student.online-school.ro', '+40740000020', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (21, 'Vlad', 'Petrescu', 'vlad.petrescu@student.online-school.ro', '+40740000021', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (22, 'Elena', 'Rusu', 'elena.rusu@student.online-school.ro', '+40740000022', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (23, 'Matei', 'Enache', 'matei.enache@student.online-school.ro', '+40740000023', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00'),
    (24, 'Sofia', 'Tudor', 'sofia.tudor@student.online-school.ro', '+40740000024', '$2a$10$jg3d2Ltol2A8tyaVg9VoNeG47.fj1iIqGv0ndQpmSxc96JfpBMTLi', b'1', NULL, '2026-04-03 09:00:00', '2026-04-03 09:00:00');

INSERT INTO user_permissions (user_id, permission)
SELECT id, 'STUDENT_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'COURSE_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'BOOK_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'BOOK_WRITE' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'ENROLMENT_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'PROFILE_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'PROFILE_WRITE' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'AUTH_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'AUTH_WRITE' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'PASSWORD_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'PASSWORD_WRITE' FROM app_user WHERE id IN (20, 21, 22, 23, 24)
UNION ALL SELECT id, 'DASHBOARD_READ' FROM app_user WHERE id IN (20, 21, 22, 23, 24);

INSERT INTO user_permissions (user_id, permission)
SELECT id, 'TEACHER_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'TEACHER_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'STUDENT_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'COURSE_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'COURSE_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'BOOK_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'ENROLMENT_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'ENROLMENT_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'PROFILE_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'PROFILE_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'AUTH_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'AUTH_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'PASSWORD_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'PASSWORD_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'DASHBOARD_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'DASHBOARD_WRITE' FROM app_user WHERE id IN (10, 11, 12, 13, 14)
UNION ALL SELECT id, 'REPORT_READ' FROM app_user WHERE id IN (10, 11, 12, 13, 14);

INSERT INTO user_permissions (user_id, permission)
SELECT id, 'STUDENT_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'STUDENT_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'TEACHER_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'TEACHER_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'ADMINISTRATOR_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'ADMINISTRATOR_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'USER_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'USER_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'COURSE_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'COURSE_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'BOOK_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'BOOK_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'ENROLMENT_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'ENROLMENT_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PROFILE_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PROFILE_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'AUTH_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'AUTH_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PASSWORD_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PASSWORD_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PERMISSION_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'PERMISSION_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'DASHBOARD_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'DASHBOARD_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'REPORT_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'REPORT_WRITE' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'SETTINGS_READ' FROM app_user WHERE id = 1
UNION ALL SELECT id, 'SETTINGS_WRITE' FROM app_user WHERE id = 1;

INSERT INTO administrator (id, employee_code, department, job_title) VALUES
    (1, 'ADM-001', 'Operations', 'Platform Administrator');

INSERT INTO teacher (id, employee_code, specialization, title, bio) VALUES
    (10, 'TCH-001', 'Mathematics', 'Senior Mathematics Teacher', 'Coordoneaza curricula de matematica pentru gimnaziu si liceu.'),
    (11, 'TCH-002', 'Computer Science', 'Software Engineering Teacher', 'Preda baze de programare, algoritmi si proiectare software.'),
    (12, 'TCH-003', 'Physics', 'Applied Physics Teacher', 'Acopera fizica experimentala si aplicatii practice in laborator.'),
    (13, 'TCH-004', 'English', 'Academic English Teacher', 'Lucreaza pe comunicare academica si pregatire pentru examene internationale.'),
    (14, 'TCH-005', 'History', 'Modern History Teacher', 'Preda istorie moderna si gandire critica bazata pe surse.');

INSERT INTO student (
    id,
    student_code,
    enrollment_date,
    date_of_birth,
    guardian_name,
    guardian_email
) VALUES
    (20, 'STD-001', '2025-09-15', '2008-03-12', 'Mihai Marinescu', 'mihai.marinescu@example.com'),
    (21, 'STD-002', '2025-09-15', '2007-11-02', 'Alina Petrescu', 'alina.petrescu@example.com'),
    (22, 'STD-003', '2025-09-15', '2008-07-24', 'Cristian Rusu', 'cristian.rusu@example.com'),
    (23, 'STD-004', '2025-09-15', '2007-05-19', 'Gabriela Enache', 'gabriela.enache@example.com'),
    (24, 'STD-005', '2025-09-15', '2008-01-30', 'Daniel Tudor', 'daniel.tudor@example.com');

INSERT INTO course (id, course_name, department) VALUES
    (100, 'Algebra Fundamentals', 'Mathematics'),
    (101, 'Java Programming Basics', 'Computer Science'),
    (102, 'Physics Lab Essentials', 'Science'),
    (103, 'Academic Writing in English', 'Languages'),
    (104, 'Modern European History', 'Humanities'),
    (105, 'Data Structures', 'Computer Science');

INSERT INTO book (id, book_name, student_id) VALUES
    (200, 'Algebra Practice Workbook', 20),
    (201, 'Java for Beginners', 21),
    (202, 'Physics Experiments Handbook', 22),
    (203, 'Academic English Companion', 23),
    (204, 'History Sourcebook', 24),
    (205, 'Advanced Data Structures Notes', 21),
    (206, 'Problem Solving in Mathematics', 20);

INSERT INTO enrolment (id, student_id, course_id) VALUES
    (300, 20, 100),
    (301, 20, 101),
    (302, 21, 101),
    (303, 21, 105),
    (304, 22, 102),
    (305, 23, 103),
    (306, 24, 104),
    (307, 24, 100);
