package com.example.asm01.controller;

import com.example.asm01.dto.request.StudentCreateRequest;
import com.example.asm01.dto.response.ApiResponse;
import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.model.Student;
import com.example.asm01.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully retrieved all students",
                        studentService.getAllStudents()
                )
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Successfully retrieved student",
                            studentService.getStudentById(id)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createStudent(@RequestBody StudentCreateRequest request) {
        studentService.createStudent(request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "created a student successfully",
                        null
                )
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentCreateRequest request
    ) {
        try {
            studentService.updateStudent(id, request);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "updated student successfully",
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }
}
