package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.entity.Allocation;
import de.philippbomers.management.students.student_manager.service.AllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * API Rest Controller to handle allocations
 */
@RestController
@RequestMapping("/api/allocation")
public class AllocationController {

    // We use constructor based injection because field based injection is not immutable

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    /**
     * Shows all allocations or throws a 409 Status
     *
     * @return List with allocations
     */
    @GetMapping
    public List<Allocation> getAllocations() {
        try {
            return this.allocationService.getAllAllocations();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There was an issue in getting allocations");
        }
    }

    /**
     * Shows allocations by ID or throws a 404 Status
     *
     * @param id allocation ID
     * @return Allocation
     */
    @GetMapping(value = "/{id}")
    public Allocation getAllocation(@PathVariable final Long id) {
        try {
            return this.allocationService.getAllocation(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Allocation not found");
        }
    }

    /**
     * Add a new allocation or throws a 409 Status
     *
     * @param allocation the allocation to add
     * @return the added allocation
     */
    @PutMapping
    public Allocation addAllocation(@Valid @RequestBody final Allocation allocation) {

        // Sets ID to null to prevent updating an existing entity
        allocation.setId(null);
        try {
            final Allocation alloc = this.allocationService.setAllocation(allocation);
            if (alloc == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Please check the time periods");
            }
            return alloc;
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Could not create allocation: " + e);
        }
    }

    /**
     * Edits an allocation or throws a 409 Status
     *
     * @param allocation the edited allocation
     * @return the updated allocation
     */
    @PatchMapping
    public Allocation editAllocation(@Valid @RequestBody final Allocation allocation) {
        try {
            this.allocationService.getAllocation(allocation.getId()).orElseThrow();
            final Allocation alloc = this.allocationService.setAllocation(allocation);
            if (alloc == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Please check the time periods");
            }
            return alloc;
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not update allocation");
        }
    }

    /**
     * Deletes an allocation or throws a 409 Status
     *
     * @param id the allocation ID to delete
     * @return deleted allocation
     */
    @DeleteMapping(value = "/{id}")
    public Allocation deleteAllocation(@PathVariable final Long id) {
        try {
            return this.allocationService.deleteAllocation(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not delete allocation");
        }
    }
}
