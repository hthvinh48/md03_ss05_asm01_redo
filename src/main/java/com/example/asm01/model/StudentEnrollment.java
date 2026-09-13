package com.example.asm01.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "student_enrollments")
public class StudentEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "student_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @JoinColumn(name = "course_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    private LocalDateTime enrolledAt;

    @PrePersist
    void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}
