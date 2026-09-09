package com.example.springbd3big.unit.service.command;

import com.example.springbd3big.config.exceptions.EmptyUpdateRequestException;
import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.config.exceptions.RequestBodyMissingException;
import com.example.springbd3big.user.student.dtos.StudentCreateRequest;
import com.example.springbd3big.user.student.dtos.StudentResponse;
import com.example.springbd3big.user.student.dtos.StudentUpdateRequest;
import com.example.springbd3big.user.student.exceptions.StudentAlreadyExistsException;
import com.example.springbd3big.user.student.exceptions.StudentNotFoundException;
import com.example.springbd3big.user.student.mappers.StudentMapper;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.student.service.command.impl.StudentCommandServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.springbd3big.unit.support.InterfaceStubSupport.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentCommandServiceImplTest {

    @Test
    void createStudent_throwsWhenBodyMissing() {
        StudentCommandServiceImpl service = new StudentCommandServiceImpl(new StudentMapper(), stub(StudentRepository.class, Map.of()));

        assertThrows(RequestBodyMissingException.class, () -> service.createStudent(null));
    }

    @Test
    void createStudent_throwsWhenEmailIsBlank() {
        StudentCommandServiceImpl service = new StudentCommandServiceImpl(new StudentMapper(), stub(StudentRepository.class, Map.of()));

        assertThrows(
                InvalidRequestException.class,
                () -> service.createStudent(new StudentCreateRequest("Ion", "Pop", " "))
        );
    }

    @Test
    void createStudent_throwsWhenEmailIsNull() {
        StudentCommandServiceImpl service = new StudentCommandServiceImpl(new StudentMapper(), stub(StudentRepository.class, Map.of()));

        assertThrows(
                InvalidRequestException.class,
                () -> service.createStudent(new StudentCreateRequest("Ion", "Pop", null))
        );
    }

    @Test
    void createStudent_throwsWhenEmailAlreadyExists() {
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "existsByEmailJPQL", args -> true
        ));

        assertThrows(
                StudentAlreadyExistsException.class,
                () -> new StudentCommandServiceImpl(new StudentMapper(), repository)
                        .createStudent(new StudentCreateRequest("Ion", "Pop", "ION@test.com"))
        );
    }

    @Test
    void createStudent_savesMappedStudent() {
        AtomicReference<Student> savedStudent = new AtomicReference<>();
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "existsByEmailJPQL", args -> false,
                "save", args -> {
                    Student student = (Student) args[0];
                    savedStudent.set(student);
                    student.setId(1);
                    return student;
                }
        ));

        StudentResponse response = new StudentCommandServiceImpl(new StudentMapper(), repository)
                .createStudent(new StudentCreateRequest("Ion", "Pop", "ION@test.com"));

        assertEquals("ION@test.com", savedStudent.get().getEmail());
        assertEquals(1, response.id());
    }

    @Test
    void updateStudent_throwsWhenMissing() {
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new StudentCommandServiceImpl(new StudentMapper(), repository)
                        .updateStudent(2L, new StudentUpdateRequest("a", "b", "c"))
        );
    }

    @Test
    void updateStudent_throwsWhenBodyMissing() {
        Student student = Student.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        student.setId(2);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        assertThrows(
                RequestBodyMissingException.class,
                () -> new StudentCommandServiceImpl(new StudentMapper(), repository).updateStudent(2L, null)
        );
    }

    @Test
    void updateStudent_throwsWhenUpdateIsEmpty() {
        Student student = Student.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        student.setId(2);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        assertThrows(
                EmptyUpdateRequestException.class,
                () -> new StudentCommandServiceImpl(new StudentMapper(), repository)
                        .updateStudent(2L, new StudentUpdateRequest(null, null, null))
        );
    }

    @Test
    void updateStudent_appliesProvidedFields() {
        Student student = Student.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        student.setId(2);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        StudentResponse response = new StudentCommandServiceImpl(new StudentMapper(), repository)
                .updateStudent(2L, new StudentUpdateRequest("New", "Other", "new@test.com"));

        assertEquals("New", student.getFirstName());
        assertEquals("Other", student.getLastName());
        assertEquals("new@test.com", student.getEmail());
        assertEquals("new@test.com", response.email());
    }

    @Test
    void updateStudent_updatesOnlyProvidedLastName() {
        Student student = Student.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        student.setId(2);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        StudentResponse response = new StudentCommandServiceImpl(new StudentMapper(), repository)
                .updateStudent(2L, new StudentUpdateRequest(null, "Other", null));

        assertEquals("Old", response.firstName());
        assertEquals("Other", response.lastName());
        assertEquals("old@test.com", response.email());
    }

    @Test
    void updateStudent_updatesOnlyProvidedEmail() {
        Student student = Student.builder().firstName("Old").lastName("Name").email("old@test.com").build();
        student.setId(2);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student)
        ));

        StudentResponse response = new StudentCommandServiceImpl(new StudentMapper(), repository)
                .updateStudent(2L, new StudentUpdateRequest(null, null, "other@test.com"));

        assertEquals("Old", response.firstName());
        assertEquals("Name", response.lastName());
        assertEquals("other@test.com", response.email());
    }

    @Test
    void deleteStudent_throwsWhenMissing() {
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.empty()
        ));

        assertThrows(
                StudentNotFoundException.class,
                () -> new StudentCommandServiceImpl(new StudentMapper(), repository).deleteStudent(3L)
        );
    }

    @Test
    void deleteStudent_deletesStudentAndReturnsDto() {
        AtomicReference<Student> deletedStudent = new AtomicReference<>();
        Student student = Student.builder().firstName("Ion").lastName("Pop").email("ion@test.com").build();
        student.setId(3);
        StudentRepository repository = stub(StudentRepository.class, Map.of(
                "findById", args -> Optional.of(student),
                "delete", args -> {
                    deletedStudent.set((Student) args[0]);
                    return null;
                }
        ));

        StudentResponse response = new StudentCommandServiceImpl(new StudentMapper(), repository).deleteStudent(3L);

        assertSame(student, deletedStudent.get());
        assertEquals(3, response.id());
    }
}
