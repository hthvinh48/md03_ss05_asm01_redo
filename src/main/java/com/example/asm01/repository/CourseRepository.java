package com.example.asm01.repository;

import com.example.asm01.dto.response.CourseResponseV2;
import com.example.asm01.model.Course;
import com.example.asm01.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByIdAndStatus(Long id, CourseStatus status);

    @Query("select c from Course c where c.status = :status")
    Page<Course> findAllByStatus(@Param("status") CourseStatus status, Pageable pageable);

    @Query("""
        select new com.example.asm01.dto.response.CourseResponseV2(c.id, c.title, c.status)
        from Course c
        where
            (:status is null or c.status = :status) and
            (lower(c.title) like lower(concat('%', :keyword, '%')))
        """)
    Page<CourseResponseV2> findAllByStatusV2(
            @Param("status") CourseStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
