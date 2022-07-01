package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.entity.Employment;
import de.philippbomers.management.students.student_manager.service.EmploymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * API Rest Controller to handle employments
 */
@RestController
@RequestMapping("/api/employment")
public class EmploymentController {

    // We use constructor based injection because field based injection is not immutable

    private final EmploymentService employmentService;

    public EmploymentController(EmploymentService employmentService) {
        this.employmentService = employmentService;
    }

    /**
     * Shows all employments or throws a 409 Status
     *
     * @return List with employments
     */
    @GetMapping
    public List<Employment> getEmployments() {
        try {
            return this.employmentService.getAllEmployments();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There was an issue in getting employments");
        }
    }

    /**
     * Shows employment by ID or throws a 404 Status
     *
     * @param id of employment
     * @return Employment
     */
    @GetMapping(value = "/{id}")
    public Employment getEmployment(@PathVariable final Long id) {
        try {
            return this.employmentService.getEmployment(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment not found");
        }
    }

    /**
     * Add a new employment or throws a 409 Status
     *
     * @param employment to add
     * @return the added employment
     */
    @PutMapping
    public Employment addEmployment(@Valid @RequestBody final Employment employment) {

        // Sets ID to null to prevent updating an existing entity
        employment.setId(null);
        try {
            return this.employmentService.setEmployment(employment);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not create employment");

        }
    }

    /**
     * Edits an employment or throws a 409 Status
     *
     * @param employment already edited
     * @return the updated employment
     */
    @PatchMapping
    public Employment editEmployment(@Valid @RequestBody final Employment employment) {
        try {
            this.employmentService.getEmployment(employment.getId()).orElseThrow();
            return this.employmentService.setEmployment(employment);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not update employment");
        }

    }

    /**
     * Deletes an employment or throws a 409 Status
     *
     * @param id of employment to delete
     * @return deleted employment
     */
    @DeleteMapping(value = "/{id}")
    public Employment deleteEmployment(@PathVariable final Long id) {
        try {
            return this.employmentService.deleteEmployment(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not delete employment");
        }
    }
}
