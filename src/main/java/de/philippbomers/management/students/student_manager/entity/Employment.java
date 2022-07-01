package de.philippbomers.management.students.student_manager.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Defines the employment of a student: f.ex. Werkstudent, Praktikant, Absolvent
 *
 * Getters, Setters, and Constructors are handled by Lombok
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The project name
     */
    @NotNull(message = "Please add a name.")
    @NotEmpty(message = "A name cannot be empty.")
    @NotBlank(message = "A name cannot be blank.")
    @Size(min = 2, max = 32, message = "A name must have between 2 and 32 characters.")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Employment that = (Employment) o;
        return id != null && Objects.equals(id, that.id);
    }
}
