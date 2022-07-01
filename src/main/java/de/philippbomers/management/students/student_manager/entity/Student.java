package de.philippbomers.management.students.student_manager.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

/**
 * Defines and declares students
 * <p>
 * Getters, Setters, and Constructors are handled by Lombok
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Students first name
     */
    @NotNull(message = "Please add a first name.")
    @NotEmpty(message = "A first name cannot be empty.")
    @NotBlank(message = "A first name cannot be blank.")
    @Size(min = 2, max = 32, message = "A first name must have between 2 and 32 characters.")
    @Pattern(regexp = "^[a-zA-Z\\d\s]*", message = "The first name includes invalid letters.")
    private String firstName;

    /**
     * Students last name
     */
    @NotNull(message = "Please add a last name.")
    @NotEmpty(message = "A last name cannot be empty.")
    @NotBlank(message = "A last name cannot be blank.")
    @Size(min = 2, max = 32, message = "A last name must have between 2 and 32 characters.")
    @Pattern(regexp = "^[a-zA-Z\\d\s]*", message = "The last name includes invalid letters.")
    private String lastName;

    /**
     * A student can have one employment. Multiple students can have the same employment
     */
    @NotNull(message = "Please select an Employment.")
    @ManyToOne
    private Employment employment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return id != null && Objects.equals(id, student.id);
    }
}
