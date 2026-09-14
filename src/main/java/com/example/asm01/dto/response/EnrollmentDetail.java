package com.example.asm01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EnrollmentDetail {
    private long id;
    private String studentName;
    private String courseName;
}
