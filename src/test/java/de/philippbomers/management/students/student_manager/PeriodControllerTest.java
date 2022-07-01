package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.entity.Period;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PeriodController.
 * <p>
 * The controller is the outermost point of an entity and therefore returns all possible errors of
 * an entity. Use service classes to work with entities until you test the controller.
 */
class PeriodControllerTest extends StudentManagerTest {

    /**
     * Tests the PUT-mapping method
     */
    @Test
    void testAddPeriod() {
        // Creates and saves a new period
        final Period period = this.createPeriod(0, 0);

        // Tests if period was successfully created
        assertNotNull(this.periodController.addPeriod(period));

        // Creates and saves a random number of periods
        IntStream.range(0, 10).forEach(
                i -> this.periodController.addPeriod(this.createPeriod(0, 0)));

        // Tests if the periods where created successfully
        assertEquals(11, this.periodService.getAllPeriods().size());

        // It should throw an Exception if adding an invalid period
        assertThrows(ResponseStatusException.class,
                () -> this.periodController.addPeriod(new Period()));
    }

    /**
     * Tests the GET-mapping method by ID
     */
    @Test
    void testGetPeriod() {
        // Creates and saves a new period
        final Period period = this.periodService.setPeriod(this.createPeriod(0, 0));

        // Tests the Get method
        assertEquals(period, this.periodController.getPeriod(period.getId()));

        // We need an atomic object to access it in IntStream
        final AtomicReference<Period> testPeriod = new AtomicReference<>(new Period());

        // Creates a random number of periods
        IntStream.range(0, (new Random()).nextInt(10, 200)).forEach(i -> testPeriod
                .set(this.periodService.setPeriod(this.createPeriod(0, 0))));

        // Tests if last period was created
        assertEquals(testPeriod.get(),
                this.periodController.getPeriod(testPeriod.get().getId()));

        // It should throw an Exception if getting a non-existent period
        assertThrows(ResponseStatusException.class,
                () -> this.periodController.getPeriod(Long.MAX_VALUE));
    }

    /**
     * Tests the GET-mapping method
     */
    @Test
    void testGetPeriods() {

        // Creates and saves multiple periods
        IntStream.range(0, 10).forEach(
                i -> this.periodService.setPeriod(this.createPeriod(0, 0)));

        // Tests if the periods are saved
        assertEquals(10, this.periodController.getPeriods().size());

        // Deletes all periods
        this.periodService.getAllPeriods()
                .forEach(period -> this.periodService.deletePeriod(period.getId()));

        // Tests if receiving an empty list if getting all periods
        assertTrue(this.periodController.getPeriods().isEmpty());
    }

    /**
     * Tests the PATCH-mapping method
     */
    @Test
    void testEditPeriod() {

        // Creates and saves a new period
        final Period period = this.periodService.setPeriod(this.createPeriod(0, 0));

        // Creates the current date
        final LocalDate localDate = LocalDate.now();

        // Edits the saved period
        period.setBegin(localDate);

        // Tests if successfully edited the period
        assertEquals(localDate, this.periodController.editPeriod(period).getBegin());

        // Tests if throws an exception if trying to edit invalid data
        period.setId(Long.MAX_VALUE);

        assertThrows(ResponseStatusException.class,
                () -> this.periodController.editPeriod(period));


        period.setBegin(period.getEnd());

        assertThrows(ResponseStatusException.class,
                () -> this.periodController.editPeriod(period));
    }

    /**
     * Tests the DELETE-mapping method
     */
    @Test
    void testDeletePeriod() {

        // Creates and saves a new period
        final Period period = this.periodService.setPeriod(this.createPeriod(0, 0));

        // Tests if successfully edited the period
        assertEquals(period, this.periodController.deletePeriod(period.getId()));

        // Creates a random number of periods
        IntStream.range(0, (new Random()).nextInt(20, 200)).forEach(
                i -> this.periodService.setPeriod(this.createPeriod(0, 0)));

        // Deletes all periods
        this.periodService.getAllPeriods()
                .forEach(per -> this.periodController.deletePeriod(per.getId()));

        // Tests if all periods were deleted
        assertTrue(this.periodService.getAllPeriods().isEmpty());

        // Tests Exception if trying to delete a non-existent period
        assertThrows(ResponseStatusException.class,
                () -> this.periodController.deletePeriod(Long.MAX_VALUE));
    }
}
