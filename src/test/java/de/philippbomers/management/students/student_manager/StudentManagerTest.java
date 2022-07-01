package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.controller.*;
import de.philippbomers.management.students.student_manager.entity.*;
import de.philippbomers.management.students.student_manager.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * DATA LOSS ALERT: DO NOT USE IN PRODUCTIVE ENVIRONMENT Only for development use with test database
 *
 * This parent class makes the controller tests easier. Testing controller methods. Tests can be run
 * independent of each other.
 *
 * To start the tests in Windows, you need to declare the JAVA_HOME variable. Further, you cannot
 * test an already running system on the same port.
 */
@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
class StudentManagerTest {

    // Field based injection to extend the class more comfortable

    @Autowired
    protected AllocationController allocationController;

    @Autowired
    protected PeriodController periodController;

    @Autowired
    protected EmploymentController employmentController;

    @Autowired
    protected de.philippbomers.management.students.student_manager.controller.FileController FileController;

    @Autowired
    protected ProjectController projectController;

    @Autowired
    protected StudentController studentController;


    @Autowired
    protected EmploymentService employmentService;

    @Autowired
    protected PeriodService periodService;

    @Autowired
    protected ProjectService projectService;

    @Autowired
    protected AllocationService allocationService;

    @Autowired
    protected StudentService studentService;

    // ID to create employments automatically
    Long employmentCreationId = 0L;

    // ID to create students automatically
    Long studentCreationId = 0L;

    // ID to create projects automatically
    Long projectCreationId = 0L;

    /**
     * The method cleans the database after each test
     */
    @BeforeEach
    void cleanDatabase() {

        // We can ignore exceptions

        try {

            this.allocationService.getAllAllocations()
                    .forEach(alloc -> this.allocationService.deleteAllocation(alloc.getId()));

        } catch (Exception e) {
            // ignore
        }

        try {

            this.studentService.getAllStudents()
                    .forEach(student -> this.studentService.deleteStudent(student.getId()));

        } catch (Exception e) {
            // ignore
        }

        try {

            this.employmentService.getAllEmployments().forEach(
                    employment -> this.employmentService.deleteEmployment(employment.getId()));

        } catch (Exception e) {
            // ignore
        }


        try {

            this.projectService.getAllProjects()
                    .forEach(project -> this.projectService.deleteProject(project.getId()));

        } catch (Exception e) {
            // ignore

        }

        try {

            this.periodService.getAllPeriods()
                    .forEach(period -> this.periodService.deletePeriod(period.getId()));

        } catch (Exception e) {
            // ignore

        }


    }

    /**
     * Creates an employment with differing names
     *
     * @return Employment
     */
    protected Employment createEmployment() {
        return Employment.builder().name("Employment" + this.employmentCreationId++).build();
    }

    /**
     * Creates a period in actual month. You can use createPeriod(0,0) to create the whole month
     *
     * @param start day in actual month
     * @param end   day in actual month
     * @return Period
     */
    protected Period createPeriod(int start, int end) {
        LocalDate localDate = LocalDate.now();

        // if day < 1 or > last day of month: use 1
        start = start < 1 || start > YearMonth.from(localDate).atEndOfMonth().getDayOfMonth()
                ? 1
                : start;

        // if day > last days of month or < 1: use last day of month
        end = end < 1 || end > YearMonth.from(localDate).atEndOfMonth().getDayOfMonth()
                ? YearMonth.from(localDate).atEndOfMonth().getDayOfMonth()
                : end;

        return Period.builder().begin(localDate.withDayOfMonth(start))
                .end(localDate.withDayOfMonth(end)).build();
    }

    /**
     * Creates a student with a new name
     *
     * @param employment the Employment to create
     * @return Student
     */
    protected Student createStudent(Employment employment) {
        return Student.builder().firstName("First" + this.studentCreationId)
                .lastName("Last" + this.studentCreationId++).employment(employment).build();
    }

    /**
     * Creates a student and automatically adds an employment to database
     *
     * @return Student
     */
    protected Student createStudent() {
        Employment employment = createEmployment();
        this.employmentService.setEmployment(employment);
        return this.createStudent(employment);
    }

    /**
     * Creates a project and automatically adds a period to database
     *
     * @return Project
     */
    protected Project createProject() {
        LocalDate localDate = LocalDate.now();

        Period period = Period.builder().begin(localDate.minusMonths(1))
                .end(localDate.plusMonths(1)).build();
        period = this.periodService.setPeriod(period);
        return Project.builder().name("Project" + this.projectCreationId++).period(period).build();
    }

    /**
     * Creates an allocation and automatically adds a project, period, and student to database
     *
     * @return Project
     */
    protected Allocation createAllocation(int start, int end) {
        Project project = this.createProject();
        project = this.projectService.setProject(project);

        Period period = this.createPeriod(start, end);
        period = this.periodService.setPeriod(period);

        Student student = this.createStudent();
        student = this.studentService.setStudent(student);

        return Allocation.builder().project(project).period(period).student(student).build();
    }
}
