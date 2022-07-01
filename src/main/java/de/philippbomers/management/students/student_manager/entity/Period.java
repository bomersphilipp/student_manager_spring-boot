package de.philippbomers.management.students.student_manager.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A supporting entity to work with time periods
 *
 * Getters, Setters, and Constructors are handled by Lombok
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Begin of the time period
     */
    @NotNull(message = "Please select a beginning date.")
    @Column(name = "period_begin")
    @DateTimeFormat
    private LocalDate begin;

    /**
     * End of the time period
     */
    @NotNull(message = "Please select an ending date.")
    @Column(name = "period_end")
    @DateTimeFormat
    private LocalDate end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Period period = (Period) o;
        return id != null && Objects.equals(id, period.id);
    }
}
