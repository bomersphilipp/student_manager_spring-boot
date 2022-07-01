package de.philippbomers.management.students.student_manager.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

/**
 * Defines and declares Projects
 *
 * Getters, Setters, and Constructors are handled by Lombok
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Project name
     */
    @NotNull(message = "Please add a name.")
    @NotEmpty(message = "A name cannot be empty.")
    @NotBlank(message = "A name cannot be blank.")
    @Size(min = 2, max = 32, message = "A name must have between 2 and 32 characters.")
    @Pattern(regexp = "^[a-zA-Z\\d\s]*", message = "The name includes invalid letters.")
    private String name;

    /**
     * The period defines when the project starts and ends
     */
    @NotNull(message = "Please add a time period.")
    @OneToOne(cascade = CascadeType.REMOVE)
    private Period period;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Project project = (Project) o;
        return id != null && Objects.equals(id, project.id);
    }
}
