package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.entity.Allocation;
import de.philippbomers.management.students.student_manager.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AllocationController.
 * <p>
 * The controller is the outermost point of an entity and therefore returns all possible errors of
 * an entity. Use service classes to work with entities until you test the controller.
 */
public class AllocationControllerTest extends StudentManagerTest {

    /**
     * Tests the PUT-mapping method
     */
    @Test
    void testAddAllocation() {

        // Creates and saves a new allocation
        final Allocation allocation = this.createAllocation(0, 0);

        // Test if student was successfully created
        assertNotNull(this.allocationController.addAllocation(allocation));

        // Creates and saves multiple allocations
        IntStream.range(0, 10).forEachOrdered(i -> this.allocationController
                .addAllocation(this.createAllocation(0, 0)));

        // Tests if the allocations are saved
        assertEquals(11, this.allocationService.getAllAllocations().size());

        // Controller throws exception if data is invalid
        assertThrows(ResponseStatusException.class,
                () -> this.allocationController.addAllocation(new Allocation()));

        this.allocationService.getAllAllocations().forEach(
                alloc -> this.allocationService.deleteAllocation(alloc.getId()));

        // check if student (allocation) period is inside projects period
        final Allocation newAllocation = this.createAllocation(0, 0);
        newAllocation.getPeriod().setBegin(
                newAllocation.getProject().getPeriod().getBegin().minusDays(1));

        assertEquals(newAllocation.getProject().getPeriod().getBegin(),
                this.allocationController.addAllocation(newAllocation).getPeriod()
                        .getBegin());

        newAllocation.getPeriod().setEnd(
                newAllocation.getProject().getPeriod().getEnd().plusDays(1));

        assertEquals(newAllocation.getProject().getPeriod().getEnd(),
                this.allocationController.addAllocation(newAllocation).getPeriod()
                        .getEnd());
    }

    /**
     * Tests the GET-mapping method by ID
     */
    @Test
    void testGetAllocation() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // Tests the Get method
        assertEquals(allocation.getId(), this.allocationController
                .getAllocation(allocation.getId()).getId());

        // We need an atomic object to access it in IntStream
        final AtomicReference<Allocation> testAllocation =
                new AtomicReference<>(new Allocation());

        // Creates and saves multiple allocations with randomization
        IntStream.range(0, (new Random()).nextInt(20, 30))
                .forEachOrdered(i -> testAllocation.set(this.allocationService
                        .setAllocation(this.createAllocation(0, 0))));

        // Checks if GET request works with last allocation
        assertEquals(testAllocation.get().getId(), this.allocationController
                .getAllocation(testAllocation.get().getId()).getId());

        // Controller throws exception if allocation could not be found
        assertThrows(ResponseStatusException.class,
                () -> this.allocationController.getAllocation(Long.MAX_VALUE));
    }

    /**
     * Tests the GET-mapping method
     */
    @Test
    void testGetAllocations() {

        // Creates and saves multiple allocations
        IntStream.range(0, 10).forEach(i -> this.allocationService
                .setAllocation(this.createAllocation(i + 1, i + 2)));

        // Tests if the allocations are saved
        assertEquals(10, this.allocationController.getAllocations().size());

        // Deletes all allocations
        this.allocationController.getAllocations().forEach(
                alloc -> this.allocationService.deleteAllocation(alloc.getId()));

        // Returns an empty list if
        assertTrue(this.allocationController.getAllocations().isEmpty());
    }


    /**
     * Tests the PATCH-mapping method
     */
    @Test
    void testEditAllocation() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // Edits the saved allocation with a new student
        final Student student = this.createStudent();
        this.studentService.setStudent(student);
        allocation.setStudent(student);

        // Tests if successfully edited the allocation
        assertEquals(student,
                this.allocationController.editAllocation(allocation).getStudent());

        allocation.setStudent(new Student());

        // Controller throws exception if you could not edit allocation
        assertThrows(ResponseStatusException.class,
                () -> this.allocationController.editAllocation(allocation));
    }

    /**
     * Tests the DELETE-mapping method
     */
    @Test
    void testDeleteAllocation() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // Tests if successfully deleted the allocation
        assertEquals(allocation,
                this.allocationController.deleteAllocation(allocation.getId()));

        // Creates and saves multiple allocations with randomization
        IntStream.range(0, (new Random()).nextInt(3, 10))
                .forEach(i -> this.allocationService.setAllocation(
                        this.createAllocation(i + 1, i + 2)));

        // Deletes all allocations
        this.allocationController.getAllocations().forEach(
                alloc -> this.allocationController.deleteAllocation(alloc.getId()));

        // Checks if all allocations were deleted
        assertTrue(allocationService.getAllAllocations().isEmpty());

        // Controller throws exception if you could not delete allocation
        assertThrows(ResponseStatusException.class,
                () -> this.allocationController.deleteAllocation(Long.MAX_VALUE));
    }

    /**
     * Tests the deletion of periods
     */
    @Test
    void testDeletePeriod() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // It should throw an Exception if someone wants to delete the period
        assertThrows(ResponseStatusException.class, () -> this.periodController
                .deletePeriod(allocation.getPeriod().getId()));
    }

    /**
     * Tests the deletion of projects
     */
    @Test
    void testDeleteProjects() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // Deletes a project
        assertEquals(allocation.getProject(), this.projectController
                .deleteProject(allocation.getProject().getId()));

        // Deletes a period
        assertEquals(allocation.getPeriod(),
                this.periodController.deletePeriod(allocation.getPeriod().getId()));

        // Period and allocation should be removed, but student should exist
        assertTrue(this.allocationService.getAllocation(allocation.getId()).isEmpty());
        assertTrue(this.studentService.getStudent(allocation.getStudent().getId())
                .isPresent());
        assertTrue(this.periodService.getPeriod(allocation.getPeriod().getId()).isEmpty());
    }

    /**
     * Tests the deletion of students
     */
    @Test
    void testDeleteStudent() {

        // Creates and saves a new allocation
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        // Deletes a student
        assertEquals(allocation.getStudent(), this.studentController
                .deleteStudent(allocation.getStudent().getId()));


        // Deletes a period
        assertEquals(allocation.getPeriod(),
                this.periodController.deletePeriod(allocation.getPeriod().getId()));

        // Allocation and period should be removed, but project should exist
        assertTrue(this.allocationService.getAllocation(allocation.getId()).isEmpty());
        assertTrue(this.periodService.getPeriod(allocation.getPeriod().getId()).isEmpty());
        assertTrue(this.projectService.getProject(allocation.getProject().getId())
                .isPresent());

    }

    /**
     * Tests to add of multiple students to one project
     */
    @Test
    void testAddStudents() {

        // Creates and saves new allocations with the same student
        final Allocation allocation =
                this.allocationService.setAllocation(this.createAllocation(0, 0));

        final Allocation newAllocation = this.createAllocation(0, 0);
        newAllocation.setStudent(allocation.getStudent());

        assertEquals(newAllocation, this.allocationService.setAllocation(newAllocation));

        // check if the student is the same in both projects
        assertEquals(allocation.getStudent(), this.allocationService
                .getAllocation(newAllocation.getId()).orElseThrow().getStudent());
    }
}
