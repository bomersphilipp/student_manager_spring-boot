package de.philippbomers.management.students.student_manager.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Associates a student to projects for specific time periods
 * <p>
 * Getters, Setters, and Constructors are handled by Lombok
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Each allocation is for one project. But projects can have multiple allocation and students.
     */
    @NotNull(message = "Please select a project.")
    @ManyToOne
    private Project project;

    /**
     * A student can have multiple periods in a project
     */
    @NotNull(message = "Please add a time period.")
    @ManyToOne
    private Period period;

    /**
     * Each allocation can have only one student. But students can have allocations to many
     * projects.
     */
    @NotNull(message = "Please choose a student.")
    @ManyToOne
    private Student student;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Allocation that = (Allocation) o;
        return id != null && Objects.equals(id, that.id);
    }
}
