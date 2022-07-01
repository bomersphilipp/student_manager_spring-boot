package de.philippbomers.management.students.student_manager;

import de.philippbomers.management.students.student_manager.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ProjectController.
 * <p>
 * The controller is the outermost point of an entity and therefore returns all possible errors of
 * an entity. Use service classes to work with entities until you test the controller.
 */
public class ProjectControllerTest extends StudentManagerTest {

    /**
     * Tests the PUT-mapping method
     */
    @Test
    void testAddProject() {

        // Tests if project was successfully created
        assertNotNull(this.projectController.addProject(this.createProject()));

        // Add multiple projects
        IntStream.range(0, 10).forEach(
                i -> this.projectController.addProject(this.createProject()));

        // Tests if all projects were created
        assertEquals(11, this.projectService.getAllProjects().size());

        // It should throw an Exception if adding an invalid project
        assertThrows(ResponseStatusException.class,
                () -> this.projectController.addProject(new Project()));
    }

    /**
     * Tests the GET-mapping method by ID
     */
    @Test
    void testGetProject() {

        // Creates and saves a new project
        final Project project = this.projectService.setProject(this.createProject());

        // Tests the Get method
        assertEquals(project, this.projectController.getProject(project.getId()));

        // We need an atomic object to access it in IntStream
        final AtomicReference<Project> testProject = new AtomicReference<>(new Project());

        // Generate a random number of projects
        IntStream.range(0, (new Random()).nextInt(20, 200)).forEach(i -> testProject
                .set(this.projectService.setProject(this.createProject())));

        // Tests if last created project is gettable
        assertEquals(testProject.get(),
                this.projectController.getProject(testProject.get().getId()));

        // It should throw an Exception if trying to get a non-existent project
        assertThrows(ResponseStatusException.class,
                () -> this.projectController.getProject(Long.MAX_VALUE));
    }

    /**
     * Tests the GET-mapping method
     */
    @Test
    void testGetProjects() {

        // Creates and saves multiple projects
        IntStream.range(0, 10)
                .forEach(i -> this.projectService.setProject(this.createProject()));

        // Tests if the projects are saved
        assertEquals(10, this.projectController.getProjects().size());

        // Deletes all projects
        this.projectService.getAllProjects().forEach(
                project -> this.projectService.deleteProject(project.getId()));

        // Checks if response returns an empty List
        assertTrue(this.projectController.getProjects().isEmpty());
    }

    /**
     * Tests the PATCH-mapping method
     */
    @Test
    void testEditProject() {

        // Creates and saves a new project
        final Project project = this.projectService.setProject(this.createProject());

        final String name = "Name";
        project.setName(name);

        // Tests if successfully edited the project
        assertEquals(name, this.projectController.editProject(project).getName());

        project.setName("");

        // It should throw an Exception if trying to edit with invalid data
        assertThrows(ResponseStatusException.class,
                () -> this.projectController.editProject(project));
    }

    /**
     * Tests the DELETE-mapping method
     */
    @Test
    void testDeleteProject() {

        // Creates and saves a new project
        final Project project = this.projectService.setProject(this.createProject());

        // Tests if successfully edited the project
        assertEquals(project, this.projectController.deleteProject(project.getId()));

        // Creates a random number of project
        IntStream.range(0, (new Random()).nextInt(20, 200))
                .forEach(i -> this.projectService.setProject(this.createProject()));

        // Deletes all projects
        this.projectService.getAllProjects().forEach(
                proj -> this.projectController.deleteProject(proj.getId()));

        // Check if all projects were deleted successfully
        assertTrue(this.projectService.getAllProjects().isEmpty());

        // It should throw an Exception if deleting a non-existent project
        assertThrows(ResponseStatusException.class,
                () -> this.projectController.deleteProject(Long.MAX_VALUE));
    }
}
