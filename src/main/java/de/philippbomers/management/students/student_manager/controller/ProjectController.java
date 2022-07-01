package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.entity.Project;
import de.philippbomers.management.students.student_manager.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * API Rest Controller to handle projects
 */
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    // We use constructor based injection because field based injection is not immutable

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Shows all projects or throws a 409 Status
     *
     * @return List with projects
     */
    @GetMapping
    public List<Project> getProjects() {
        try {
            return this.projectService.getAllProjects();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "There was an issue in getting projects");
        }
    }

    /**
     * Shows a project by ID or throws a 404 Status
     *
     * @param id of project
     * @return Project
     */
    @GetMapping(value = "/{id}")
    public Project getProject(@PathVariable final Long id) {
        try {
            return this.projectService.getProject(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
    }

    /**
     * Add a new project or throws a 409 Status
     *
     * @param project so add
     * @return the added project
     */
    @PutMapping
    public Project addProject(@Valid @RequestBody final Project project) {

        // Sets ID to null to prevent updating an existing entity
        project.setId(null);
        try {
            return this.projectService.setProject(project);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not add project");
        }
    }

    /**
     * Edits a project or throws a 409 Status
     *
     * @param project to edit
     * @return the updated project
     */
    @PatchMapping
    public Project editProject(@Valid @RequestBody final Project project) {
        try {
            this.projectService.getProject(project.getId()).orElseThrow();
            return this.projectService.setProject(project);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not update project");
        }
    }

    /**
     * Deletes a project and its period or throws a 409 Status
     *
     * @param id of project to delete
     * @return the deleted project
     */
    @DeleteMapping(value = "/{id}")
    public Project deleteProject(@PathVariable final Long id) {
        try {
            return this.projectService.deleteProject(id).orElseThrow();
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete project");
        }
    }
}
