package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.entity.Student;
import de.philippbomers.management.students.student_manager.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * API Rest Controller to handle students
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    // We use constructor based injection because field based injection is not immutable

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Shows all students or throws a 409 Status
     *
     * @return List with students
     */
    @GetMapping
    public List<Student> getStudents() {
        try {
            return this.studentService.getAllStudents();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There was an issue in getting students");
        }
    }

    /**
     * Shows student by ID or throws a 404 Status
     *
     * @param id of student
     * @return Student
     */
    @GetMapping(value = "/{id}")
    public Student getStudent(@PathVariable final Long id) {
        try {
            return this.studentService.getStudent(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }

    /**
     * Add a new student or throws a 409 Status
     *
     * @param student to add or update
     * @return the added student
     */
    @PutMapping
    public Student addStudent(@Valid @RequestBody final Student student) {

        // Sets ID to null to prevent updating an existing entity
        student.setId(null);
        try {
            return this.studentService.setStudent(student);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not create student");
        }
    }

    /**
     * Edits a student or throws a 409 Status
     *
     * @param student to edit
     * @return the updated student
     */
    @PatchMapping
    public Student editStudent(@Valid @RequestBody final Student student) {
        try {
            this.studentService.getStudent(student.getId()).orElseThrow();
            return this.studentService.setStudent(student);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not update student");
        }
    }

    /**
     * Delete a student or throws a 409 Status
     *
     * @param id of student to delete
     * @return deleted student
     */
    @DeleteMapping(value = "/{id}")
    public Student deleteStudent(@PathVariable final Long id) {
        try {
            return this.studentService.deleteStudent(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not delete student");
        }
    }
}
