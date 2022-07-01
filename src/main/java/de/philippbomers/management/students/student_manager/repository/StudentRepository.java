package de.philippbomers.management.students.student_manager.repository;

import de.philippbomers.management.students.student_manager.entity.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides database requests. We only need to add requests that are not in the CrudRepository
 */
@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

    // Needs to be included for ensuring receiving the correct type
    List<Student> findAll();
}
