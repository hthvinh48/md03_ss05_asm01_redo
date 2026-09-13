package com.example.asm01.repository;

import com.example.asm01.model.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
    void deleteByCourseIdAndStudentId(Long courseId, Long studentId);
    List<StudentEnrollment> findByCourseId(Long courseId);
}
