package com.example.asm01.repository;

import com.example.asm01.model.Course;
import com.example.asm01.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByIdAndStatus(Long id, CourseStatus status);
}
