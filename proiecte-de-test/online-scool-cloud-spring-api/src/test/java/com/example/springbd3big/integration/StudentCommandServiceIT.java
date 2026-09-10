package com.example.springbd3big.integration;

import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.command.StudentCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StudentCommandServiceIT {

    @Autowired
    private StudentCommandService studentCommandService;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @Transactional
    void createStudent_persistsAndReturnsDto() {
        StudentCreateRequest req = new StudentCreateRequest(
                "Ion",
                "Popescu",
                "ion.popescu@test.com"
        );

        StudentResponse created = studentCommandService.createStudent(req);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("Ion", created.firstName());
        assertEquals("Popescu", created.lastName());
        assertEquals("ion.popescu@test.com", created.email());
        assertTrue(studentRepository.findById((long) created.id()).isPresent());
    }

    @Test
    @Transactional
    void updateStudent_updatesFieldsAndReturnsDto() {
        Student saved = studentRepository.save(Student.builder()
                .firstName("Ana")
                .lastName("Ionescu")
                .email("ana.ionescu@test.com")
                .build());

        StudentUpdateRequest req = new StudentUpdateRequest("A", "B", "C");
        StudentResponse updated = studentCommandService.updateStudent((long) saved.getId(), req);

        assertNotNull(updated);
        assertEquals("A", updated.firstName());
        assertEquals("B", updated.lastName());
        assertEquals("C", updated.email());
    }

    @Test
    @Transactional
    void deleteStudent_removesEntity() {
        Student saved = studentRepository.save(Student.builder()
                .firstName("Mihai")
                .lastName("Georgescu")
                .email("mihai.georgescu@test.com")
                .build());

        Long id = (long) saved.getId();
        StudentResponse deleted = studentCommandService.deleteStudent(id);

        assertNotNull(deleted);
        assertFalse(studentRepository.findById(id).isPresent());
    }
}
