package com.example.asm01.repository;

import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContains(String name);

    @Query("""
        select new com.example.asm01.dto.response.StudentResponse (s.id, s.name, s.email)
        from Student s
        where
            (:name is null or lower(s.name) like lower(concat('%', :name, '%'))) and
            (:email is null or lower(s.email) like lower(concat('%', :email, '%')))
        """)
    Page<StudentResponse> findAllByNameOrEmail(
            @Param("name") String name,
            @Param("email") String email,
            Pageable pageable
    );
}
