package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StudentController.
 *
 * The controller is the outermost point of an entity and therefore returns all possible errors of
 * an entity. Use service classes to work with entities until you test the controller.
 */
public class StudentControllerTest extends StudentManagerTest {

    /**
     * Tests the PUT-mapping method
     */
    @Test
    void testAddStudent() {

        // Tests student creation
        assertNotNull(this.studentController.addStudent(this.createStudent()));

        // Adds multiple students
        IntStream.range(0, 10).forEach(
                i -> this.studentController.addStudent(this.createStudent()));

        // Checks if all students were created
        assertEquals(11, this.studentService.getAllStudents().size());

        // It should throw an Exception if adding an invalid student
        assertThrows(ResponseStatusException.class,
                () -> this.studentController.addStudent(new Student()));
    }

    /**
     * Tests the GET-mapping method by ID
     */
    @Test
    void testGetStudent() {

        // Creates and saves a new student
        final Student student = this.studentService.setStudent(this.createStudent());

        // Tests the Get method
        assertEquals(student, this.studentController.getStudent(student.getId()));

        // We need an atomic object to access it in IntStream
        final AtomicReference<Student> testStudent = new AtomicReference<>(new Student());

        // Creates a random number of students
        IntStream.range(0, (new Random()).nextInt(20, 200)).forEach(i -> testStudent
                .set(this.studentService.setStudent(this.createStudent())));

        // Test if we can get the last student
        assertEquals(testStudent.get(),
                this.studentController.getStudent(testStudent.get().getId()));

        // It should throw an Exception if getting a non-existent student
        assertThrows(ResponseStatusException.class,
                () -> this.studentController.getStudent(Long.MAX_VALUE));
    }

    /**
     * Tests the GET-mapping method
     */
    @Test
    void testGetStudents() {

        // Creates and saves multiple students
        IntStream.range(0, 10)
                .forEach(i -> this.studentService.setStudent(this.createStudent()));

        // Tests if the students are saved
        assertEquals(10, this.studentController.getStudents().size());

        // Delete all students
        this.studentService.getAllStudents().forEach(
                student -> this.studentService.deleteStudent(student.getId()));

        // Tests if controller returns an empty list
        assertTrue(this.studentController.getStudents().isEmpty());
    }

    /**
     * Tests the PATCH-mapping method
     */
    @Test
    void testEditStudent() {

        // Creates and saves a new student
        final Student student = this.studentService.setStudent(this.createStudent());

        final String name = "Name";
        student.setFirstName(name);

        // Tests if successfully edited the student
        assertEquals(name, this.studentController.editStudent(student).getFirstName());

        // It should throw an Exception if edit with invalid properties
        student.setFirstName("");

        assertThrows(ResponseStatusException.class,
                () -> this.studentController.editStudent(student));
    }

    /**
     * Tests the DELETE-mapping method
     */
    @Test
    void testDeleteStudent() {

        // Creates and saves a new student
        final Student student = this.studentService.setStudent(this.createStudent());

        // Tests if successfully edited the student
        assertEquals(student, this.studentController.deleteStudent(student.getId()));

        // Creates and saves a random number of students
        IntStream.range(0, (new Random()).nextInt(20, 200))
                .forEach(i -> this.studentService.setStudent(this.createStudent()));

        // Deletes all students
        this.studentService.getAllStudents().forEach(
                stud -> this.studentController.deleteStudent(stud.getId()));

        // Tests if all deletions were successfully
        assertTrue(this.studentService.getAllStudents().isEmpty());

        // It should throw an Exception if deleting a non-existent student
        assertThrows(ResponseStatusException.class,
                () -> this.studentController.deleteStudent(Long.MAX_VALUE));
    }
}
