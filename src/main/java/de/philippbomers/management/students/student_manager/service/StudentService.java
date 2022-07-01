package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.Student;
import de.philippbomers.management.students.student_manager.repository.AllocationRepository;
import de.philippbomers.management.students.student_manager.repository.EmploymentRepository;
import de.philippbomers.management.students.student_manager.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The service class provides a connection between the Controller and Repository and can include
 * further logic for validation, data evaluation and processing
 */
@Service
public class StudentService {

    // We use constructor based injection because field based injection is not immutable

    private final StudentRepository studentRepository;

    private final EmploymentRepository employmentRepository;

    private final AllocationRepository allocationRepository;

    public StudentService(StudentRepository studentRepository, EmploymentRepository employmentRepository, AllocationRepository allocationRepository) {
        this.studentRepository = studentRepository;
        this.employmentRepository = employmentRepository;
        this.allocationRepository = allocationRepository;
    }

    /**
     * @return List with all students
     */
    public List<Student> getAllStudents() {
        return this.studentRepository.findAll();
    }

    /**
     * @param id of student
     * @return Optional<Student>
     */
    public Optional<Student> getStudent(final Long id) {
        return this.studentRepository.findById(id);
    }

    /**
     * @param student to edit or add
     * @return the saved student
     */
    public Student setStudent(final Student student) {
        student.setEmployment(this.employmentRepository.save(student.getEmployment()));
        return this.studentRepository.save(student);
    }

    /**
     * Deletes a student by ID and returns the deleted student
     *
     * @param id of student to delete
     * @return the deleted student
     */
    public Optional<Student> deleteStudent(final Long id) {
        final Optional<Student> student = this.studentRepository.findById(id);
        if (student.isPresent()) {

            // Deletes all allocations with that student
            this.allocationRepository.findAll().stream()
                    .filter(alloc -> alloc.getStudent().equals(student.get()))
                    .forEach(alloc -> this.allocationRepository.deleteById(alloc.getId()));

            this.studentRepository.deleteById(id);
        }
        return student;
    }
}
