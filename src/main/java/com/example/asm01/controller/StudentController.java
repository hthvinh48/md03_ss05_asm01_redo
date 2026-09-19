package com.example.asm01.controller;

import com.example.asm01.dto.request.StudentCreateRequest;
import com.example.asm01.dto.response.ApiResponse;
import com.example.asm01.dto.response.PageResponse;
import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.service.StudentService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction direction
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully retrieved all students",
                        studentService.getAllStudentsPage(page, size, name, email, sortBy, direction)
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

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "deleted student successfully",
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
