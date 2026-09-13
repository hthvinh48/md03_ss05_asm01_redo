package com.example.asm01.dto.response;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseEnrollmentResponse {
    private Long studentId;
    private Long courseId;
    private LocalDateTime enrolledAt;
}
