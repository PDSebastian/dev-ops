package com.example.springbd3big.integration.http;

import com.example.springbd3big.book.model.Book;
import com.example.springbd3big.book.repository.BookRepository;
import com.example.springbd3big.config.security.JwtService;
import com.example.springbd3big.course.model.Course;
import com.example.springbd3big.course.repository.CourseRepository;
import com.example.springbd3big.enrolment.model.Enrolment;
import com.example.springbd3big.enrolment.repository.EnrolmentRepository;
import com.example.springbd3big.user.student.model.Student;
import com.example.springbd3big.user.student.repository.StudentRepository;
import com.example.springbd3big.user.administrator.model.Administrator;
import com.example.springbd3big.user.model.Permission;
import com.example.springbd3big.user.model.PermissionTemplates;
import com.example.springbd3big.user.teacher.model.Teacher;
import com.example.springbd3big.user.model.User;
import com.example.springbd3big.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class AbstractEndpointIntegrationTest {

    protected static final String DEFAULT_PASSWORD = "password123";

    private final AtomicInteger sequence = new AtomicInteger();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected CourseRepository courseRepository;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected EnrolmentRepository enrolmentRepository;

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected String bearerTokenFor(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }

    protected Administrator persistAdministrator() {
        String marker = nextMarker("admin");
        Administrator administrator = Administrator.builder()
                .firstName("Admin" + marker)
                .lastName("User" + marker)
                .email("admin-" + marker + "@example.com")
                .phoneNumber("+4000000" + marker)
                .employeeCode("ADM-" + marker)
                .department("Operations")
                .jobTitle("Platform Admin")
                .permissions(PermissionTemplates.administratorDefaults())
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .active(true)
                .build();
        userRepository.saveAndFlush(administrator);
        return administrator;
    }

    protected Teacher persistTeacher() {
        String marker = nextMarker("teacher");
        Teacher teacher = Teacher.builder()
                .firstName("Teacher" + marker)
                .lastName("User" + marker)
                .email("teacher-" + marker + "@example.com")
                .phoneNumber("+4000001" + marker)
                .employeeCode("TCH-" + marker)
                .specialization("Computer Science")
                .title("Senior Teacher")
                .bio("Teaches software engineering fundamentals.")
                .permissions(PermissionTemplates.teacherDefaults())
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .active(true)
                .build();
        userRepository.saveAndFlush(teacher);
        return teacher;
    }

    protected Student persistStudent() {
        String marker = nextMarker("student");
        Student student = Student.builder()
                .firstName("Student" + marker)
                .lastName("User" + marker)
                .email("student-" + marker + "@example.com")
                .phoneNumber("+4000002" + marker)
                .permissions(PermissionTemplates.studentDefaults())
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .active(true)
                .build();
        return studentRepository.saveAndFlush(student);
    }

    protected Course persistCourse(String courseName, String department) {
        Course course = Course.builder()
                .courseName(courseName)
                .departament(department)
                .build();
        return courseRepository.saveAndFlush(course);
    }

    protected Book persistBook(String bookName, Student student) {
        Book book = Book.builder()
                .bookName(bookName)
                .student(student)
                .build();
        return bookRepository.saveAndFlush(book);
    }

    protected Enrolment persistEnrolment(Student student, Course course) {
        Enrolment enrolment = Enrolment.builder()
                .student(student)
                .course(course)
                .build();
        return enrolmentRepository.saveAndFlush(enrolment);
    }

    private String nextMarker(String prefix) {
        return prefix + "-" + sequence.incrementAndGet();
    }
}
