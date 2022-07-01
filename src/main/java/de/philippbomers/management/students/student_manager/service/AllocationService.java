package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.Allocation;
import de.philippbomers.management.students.student_manager.repository.AllocationRepository;
import de.philippbomers.management.students.student_manager.repository.PeriodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The service class provides a connection between the Controller and Repository and can include
 * further logic for validation, data evaluation and processing
 */
@Service
public class AllocationService {

    // We use constructor based injection because field based injection is not immutable

    private final AllocationRepository allocationRepository;

    private final PeriodService periodService;

    private final StudentService studentService;

    private final PeriodRepository periodRepository;

    public AllocationService(AllocationRepository allocationRepository, PeriodService periodService, StudentService studentService, PeriodRepository periodRepository) {
        this.allocationRepository = allocationRepository;
        this.periodService = periodService;
        this.studentService = studentService;
        this.periodRepository = periodRepository;
    }

    /**
     * @return List with all allocations
     */
    public List<Allocation> getAllAllocations() {
        return this.allocationRepository.findAll();
    }

    /**
     * @param id of allocation
     * @return Optional<Allocation>
     */
    public Optional<Allocation> getAllocation(final Long id) {
        return this.allocationRepository.findById(id);
    }

    /**
     * @param allocation new or edited allocation
     * @return the saved allocation
     */
    public Allocation setAllocation(final Allocation allocation) {

        // Ensures that student (allocation) period is inside projects period
        if (allocation.getPeriod().getBegin()
                .isBefore(allocation.getProject().getPeriod().getBegin())) {
            allocation.getPeriod().setBegin(allocation.getProject().getPeriod().getBegin());
        }

        if (allocation.getPeriod().getEnd().isAfter(allocation.getProject().getPeriod().getEnd())) {
            allocation.getPeriod().setEnd(allocation.getProject().getPeriod().getEnd());
        }

        // The allocations must have different times
        if (allocation.getPeriod().getBegin().equals(allocation.getPeriod().getEnd())) {
            return null;
        }

        allocation.setPeriod(this.periodService.setPeriod(allocation.getPeriod()));
        allocation.setStudent(this.studentService.setStudent(allocation.getStudent()));
        return this.allocationRepository.save(allocation);
    }

    /**
     * Deletes an allocation by ID and returns the deleted allocation
     *
     * @param id id of allocation to delete
     * @return the deleted allocation
     */
    public Optional<Allocation> deleteAllocation(final Long id) {
        final Optional<Allocation> allocation = this.allocationRepository.findById(id);
        allocation.ifPresent(value -> {
            this.allocationRepository.deleteById(id);
            this.periodRepository.delete(value.getPeriod());
        });
        return allocation;
    }
}
