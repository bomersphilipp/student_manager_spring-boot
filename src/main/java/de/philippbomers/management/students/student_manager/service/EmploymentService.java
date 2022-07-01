package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.Employment;
import de.philippbomers.management.students.student_manager.repository.EmploymentRepository;
import de.philippbomers.management.students.student_manager.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The service class provides a connection between the Controller and Repository and can include
 * further logic for validation, data evaluation and processing
 */
@Service
public class EmploymentService {

    // Annotation injection

    private final EmploymentRepository employmentRepository;

    private final StudentRepository studentRepository;

    public EmploymentService(EmploymentRepository employmentRepository, StudentRepository studentRepository) {
        this.employmentRepository = employmentRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * @return List with all employments
     */
    public List<Employment> getAllEmployments() {
        return this.employmentRepository.findAll();
    }

    /**
     * @param id of employment
     * @return Optional<Employment>
     */
    public Optional<Employment> getEmployment(final Long id) {
        return this.employmentRepository.findById(id);
    }

    /**
     * @param employment edited employment
     * @return the saved employment
     */
    public Employment setEmployment(final Employment employment) {
        return this.employmentRepository.save(employment);
    }

    /**
     * Deletes an employment by ID and returns the deleted employment
     *
     * @param id of employment to delete
     * @return the deleted employment
     */
    public Optional<Employment> deleteEmployment(final Long id) {
        final Optional<Employment> employment = this.employmentRepository.findById(id);

        if (employment.isPresent()) {

            // Checks if there is not any student with that employment.
            // if there is a student, return null to give the information that it is not
            // deletable
            if (this.studentRepository.findAll().stream()
                    .anyMatch(student -> student.getEmployment().equals(employment.get()))) {
                return Optional.empty();
            }

            this.employmentRepository.deleteById(id);
        }

        return employment;
    }
}
