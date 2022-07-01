package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.Period;
import de.philippbomers.management.students.student_manager.repository.AllocationRepository;
import de.philippbomers.management.students.student_manager.repository.PeriodRepository;
import de.philippbomers.management.students.student_manager.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The service class provides a connection between the Controller and Repository and can include
 * further logic for validation, data evaluation and processing
 */
@Service
public class PeriodService {

    // Annotation injection

    private final PeriodRepository periodRepository;

    private final ProjectRepository projectRepository;

    private final AllocationRepository allocationRepository;

    public PeriodService(PeriodRepository periodRepository, ProjectRepository projectRepository, AllocationRepository allocationRepository) {
        this.periodRepository = periodRepository;
        this.projectRepository = projectRepository;
        this.allocationRepository = allocationRepository;
    }

    /**
     * @return List with all periods
     */
    public List<Period> getAllPeriods() {
        return this.periodRepository.findAll();
    }

    /**
     * @param id of period
     * @return Optional<Period>
     */
    public Optional<Period> getPeriod(final Long id) {
        return this.periodRepository.findById(id);
    }

    /**
     * @param period to update or add
     * @return the saved period
     */
    public Period setPeriod(final Period period) {

        // Begin must be smaller than end
        if (period.getBegin().isAfter(period.getEnd())
                || period.getBegin().isEqual(period.getEnd())) {
            return null;
        }

        return this.periodRepository.save(period);
    }

    /**
     * Deletes a period by ID and returns the deleted period
     *
     * @param id of period to delete
     * @return the deleted period
     */
    public Optional<Period> deletePeriod(final Long id) {
        final Optional<Period> period = this.periodRepository.findById(id);
        if (period.isPresent()) {

            // Checks if it is not connected with any project.
            // If there is a hit, return null to give the information that the period is not
            // deletable
            if (this.projectRepository.findAll().stream()
                    .anyMatch(project -> project.getPeriod().equals(period.get()))
                    || this.allocationRepository.findAll().stream()
                    .anyMatch(alloc -> alloc.getPeriod().equals(period.get()))) {
                return Optional.empty();
            }

            this.periodRepository.deleteById(id);
        }
        return period;
    }
}
