package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.entity.Period;
import de.philippbomers.management.students.student_manager.service.PeriodService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * API Rest Controller to handle periods
 */
@RestController
@RequestMapping("/api/period")
public class PeriodController {

    // We use constructor based injection because field based injection is not immutable

    private final PeriodService periodService;

    public PeriodController(PeriodService periodService) {
        this.periodService = periodService;
    }

    /**
     * Shows all periods or throws a 409 Status
     *
     * @return List with periods
     */
    @GetMapping
    public List<Period> getPeriods() throws ResponseStatusException {
        try {
            return this.periodService.getAllPeriods();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There was an issue in getting periods");
        }
    }

    /**
     * Shows period by ID or throws a 404 Status
     *
     * @param id of period
     * @return Period
     */
    @GetMapping(value = "/{id}")
    public Period getPeriod(@PathVariable final Long id) throws ResponseStatusException {
        try {
            return this.periodService.getPeriod(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Period not found");
        }
    }

    /**
     * Add a new period or throws a 409 Status
     *
     * @param period to add or update
     * @return the added period
     */
    @PutMapping
    public Period addPeriod(@Valid @RequestBody final Period period) throws ResponseStatusException {

        // Sets ID to null to prevent updating an existing entity
        period.setId(null);
        try {
            return this.periodService.setPeriod(period);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not Create period");
        }
    }

    /**
     * Edits a period or throws a 409 Status
     *
     * @param period to edit
     * @return the updated period
     */
    @PatchMapping
    public Period editPeriod(@Valid @RequestBody final Period period) throws ResponseStatusException {
        try {
            this.periodService.getPeriod(period.getId()).orElseThrow();
            return this.periodService.setPeriod(period);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not update period: " + e);
        }
    }

    /**
     * Deletes a period or throws a 409 Status
     *
     * @param id of period to delete
     * @return deleted period
     */
    @DeleteMapping(value = "/{id}")
    public Period deletePeriod(@PathVariable final Long id) throws ResponseStatusException {
        try {
            return this.periodService.deletePeriod(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete period");
        }
    }
}
