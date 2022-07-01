package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.Project;
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
public class ProjectService {

    // We use constructor based injection because field based injection is not immutable

    private final ProjectRepository projectRepository;

    private final PeriodRepository periodRepository;

    private final AllocationRepository allocationRepository;

    public ProjectService(ProjectRepository projectRepository, PeriodRepository periodRepository, AllocationRepository allocationRepository) {
        this.projectRepository = projectRepository;
        this.periodRepository = periodRepository;
        this.allocationRepository = allocationRepository;
    }

    /**
     * @param project Project to add or update
     * @return the saved project
     */
    public Project setProject(final Project project) {
        project.setPeriod(this.periodRepository.save(project.getPeriod()));
        return this.projectRepository.save(project);
    }

    /**
     * @param id project ID
     * @return Optional<Project>
     */
    public Optional<Project> getProject(final Long id) {
        return this.projectRepository.findById(id);
    }

    /**
     * @return List with all projects
     */
    public List<Project> getAllProjects() {
        return this.projectRepository.findAll();
    }

    /**
     * Deletes a project by ID and returns the deleted project
     *
     * @param id of project to delete
     * @return the deleted project
     */
    public Optional<Project> deleteProject(final Long id) {
        final Optional<Project> project = this.projectRepository.findById(id);
        project.ifPresent(value -> {

            // Deletes all allocations for that project
            this.allocationRepository.findAll().stream()
                    .filter(alloc -> alloc.getProject().equals(value))
                    .forEach(alloc -> allocationRepository.deleteById(alloc.getId()));

            this.projectRepository.deleteById(id);
        });
        return project;
    }
}
