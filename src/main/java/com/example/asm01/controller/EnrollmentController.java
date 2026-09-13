package com.example.asm01.controller;

import com.example.asm01.dto.EnrollmentDetail;
import com.example.asm01.dto.request.StudentEnrollmentRequest;
import com.example.asm01.dto.response.StudentEnrollmentResponse;
import com.example.asm01.model.StudentEnrollment;
import com.example.asm01.dto.response.ApiResponse;
import com.example.asm01.service.StudentEnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {
    private final StudentEnrollmentService studentEnrollmentService;

    public EnrollmentController(StudentEnrollmentService studentEnrollmentService) {
        this.studentEnrollmentService = studentEnrollmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentDetail>>> findAll() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetched data successfully",
                        studentEnrollmentService.findAllStudentEnrollments()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "fetched data successfully",
                            studentEnrollmentService.findById(id)
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
    public ResponseEntity<ApiResponse<Void>> createStudentEnrollment(
            @RequestBody StudentEnrollmentRequest req
    ) {
        try {
            studentEnrollmentService.createStudentEnrolment(
                    req.getCourseId(),
                    req.getStudentId()
            );
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "created data successfully",
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentEnrollment>> delete(@PathVariable Long id) {
        try {
            studentEnrollmentService.deleteStudentEnrolment(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "delete enrollment successfully",
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateStudentEnrollment(
            @PathVariable Long id,
            @RequestBody StudentEnrollmentRequest request
    ) {
        try {
            studentEnrollmentService.updateStudentEnrolment(request.getStudentId(), request.getCourseId(), id);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "updated enrollment successfully",
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }
}
