package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.entity.Employment;
import de.philippbomers.management.students.student_manager.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EmploymentController.
 * <p>
 * The controller is the outermost point of an entity and therefore returns all possible errors of
 * an entity. Use service classes to work with entities until you test the controller.
 */
class EmploymentControllerTest extends StudentManagerTest {

    /**
     * Tests the PUT-mapping method
     */
    @Test
    void testAddEmployment() {

        // Creates and saves a new employment
        final Employment employment = this.createEmployment();

        // Test if employment was successfully created
        assertNotNull(this.employmentController.addEmployment(employment));

        // Creates and saves multiple employments
        IntStream.range(0, 10).forEach(
                i -> this.employmentService.setEmployment(this.createEmployment()));

        // Tests creation of multiple employments
        assertEquals(11, this.employmentService.getAllEmployments().size());

        // It should throw an Exception if adding an invalid employment
        assertThrows(ResponseStatusException.class,
                () -> this.employmentController.addEmployment(new Employment()));
    }

    /**
     * Tests the GET-mapping method by ID
     */
    @Test
    void testGetEmployment() {

        // Creates and saves a new employment
        final Employment employment =
                this.employmentService.setEmployment(this.createEmployment());

        // Tests the GET by ID method
        assertEquals(employment,
                this.employmentController.getEmployment(employment.getId()));

        // We need an atomic object to access it in IntStream
        final AtomicReference<Employment> testEmployment =
                new AtomicReference<>(new Employment());

        // Creates a random number of employments
        IntStream.range(0, (new Random()).nextInt(20, 30)).forEach(i -> testEmployment.set(
                this.employmentService.setEmployment(this.createEmployment())));

        // Tests if the GET request works for the last created object
        assertEquals(testEmployment.get(), this.employmentController
                .getEmployment(testEmployment.get().getId()));

        // It should throw an Exception if you could not get an employment
        assertThrows(ResponseStatusException.class,
                () -> this.employmentController.getEmployment(Long.MAX_VALUE));
    }

    /**
     * Tests the GET-mapping method
     */
    @Test
    void testGetEmployments() {

        // Creates and saves multiple employments
        IntStream.range(0, 10).forEach(
                i -> this.employmentService.setEmployment(this.createEmployment()));

        // Tests if the employments are saved
        assertEquals(10, this.employmentController.getEmployments().size());

        // Deletes all employments
        this.employmentService.getAllEmployments()
                .forEach(employment -> this.employmentService
                        .deleteEmployment(employment.getId()));

        // Tests if an empty database returns an empty list
        assertTrue(this.employmentController.getEmployments().isEmpty());
    }

    /**
     * Tests the PATCH-mapping method
     */
    @Test
    void testEditEmployment() {

        // Creates and saves a new employment
        final Employment employment =
                this.employmentService.setEmployment(this.createEmployment());

        // Edits the saved employment
        final String name = "Name";
        employment.setName(name);

        // Tests if successfully edited the employment
        assertEquals(name, this.employmentController.editEmployment(employment).getName());

        // Tests if throws exception on patching invalid data
        employment.setName("");

        assertThrows(ResponseStatusException.class,
                () -> this.employmentController.editEmployment(employment));
    }

    /**
     * Tests the DELETE-mapping method
     */
    @Test
    void testDeleteEmployment() {
        // Creates and saves a new employment
        final Employment employment =
                this.employmentService.setEmployment(this.createEmployment());

        // Tests if successfully edited the employment
        assertEquals(employment,
                this.employmentController.deleteEmployment(employment.getId()));

        assertNotEquals(employment,
                this.employmentService.getEmployment(employment.getId()));

        // Creates a random number of employments
        IntStream.range(0, (new Random()).nextInt(20, 100)).forEach(
                i -> this.employmentService.setEmployment(this.createEmployment()));

        // Delete all employments
        this.employmentService.getAllEmployments().forEach(
                emp -> this.employmentController.deleteEmployment(emp.getId()));

        // Test if all employments are deleted
        assertTrue(this.employmentService.getAllEmployments().isEmpty());

        // It should throw an Exception if deleting a non-existent employment
        assertThrows(ResponseStatusException.class,
                () -> this.employmentController.deleteEmployment(Long.MAX_VALUE));
    }

    /**
     * Tests the deletion of an employment if a student with that employment is existing
     */
    @Test
    void testDeleteEmploymentWithStudent() {

        // Creates new student and employment
        Student student = this.createStudent();
        student = this.studentService.setStudent(student);

        final AtomicReference<Student> testStudent = new AtomicReference<>(student);

        // It should throw an Exception if someone wants to delete the employment
        assertThrows(ResponseStatusException.class, () -> this.employmentController
                .deleteEmployment(testStudent.get().getEmployment().getId()));
    }
}
