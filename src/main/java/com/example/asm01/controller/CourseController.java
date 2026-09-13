package com.example.asm01.controller;

import com.example.asm01.dto.request.CourseCreateRequest;
import com.example.asm01.dto.request.CourseDropoutRequest;
import com.example.asm01.dto.request.CourseEnrollmentRequest;
import com.example.asm01.dto.request.CourseUpdateRequest;
import com.example.asm01.dto.response.CourseEnrollmentResponse;
import com.example.asm01.dto.response.CourseResponse;
import com.example.asm01.dto.response.ApiResponse;
import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> findAll() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetched course data successfully",
                        courseService.findAllCourses()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "fetched course successfully",
                            courseService.findCourseById(id)
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
    public ResponseEntity<ApiResponse<Void>> insert(@RequestBody CourseCreateRequest course) {
        try {
            courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                            true,
                            "course created successfully",
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            false,
                            "failed to create course",
                            null
                    )
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Long id, @RequestBody CourseUpdateRequest course) {
        try {
            courseService.updateCourse(id, course);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "updated course successfully",
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            courseService.deleteCourseById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "deleted course successfully",
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

    @PostMapping("/{id}/enrollments")
    public ResponseEntity<ApiResponse<CourseEnrollmentResponse>> enrollCourse(
            @PathVariable Long id,
            @RequestBody CourseEnrollmentRequest req
    ) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "enrolled course successfully",
                            courseService.enrollCourse(id, req)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    )
            );
        }
    }

    @DeleteMapping("/{courseId}/students")
    public ResponseEntity<ApiResponse<Void>> deleteEnrollment(
            @PathVariable Long courseId,
            @RequestBody CourseDropoutRequest request
    ) {
        try {
            courseService.deleteStudentEnrollment(courseId, request);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    new ApiResponse<>(
                            true,
                            "deleted course successfully",
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

    @GetMapping("/{courseId}/enrollments/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> findStudentInCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "data",
                        courseService.getStudentsByCourseId(courseId, search)
                )
        );
    }
}
