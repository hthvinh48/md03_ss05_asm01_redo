package com.example.asm01.controller;

import com.example.asm01.dto.request.InstructorCreateRequest;
import com.example.asm01.dto.InstructorDetail;
import com.example.asm01.dto.request.InstructorUpdateRequest;
import com.example.asm01.model.Instructor;
import com.example.asm01.dto.response.ApiResponse;
import com.example.asm01.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstructorDetail>>> findAllInstructors() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Fetched instructor details successfully",
                        instructorService.getInstructorDetail()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstructorDetail>> findInstructorById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "get data successfully",
                            instructorService.findInstructorById(id)
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
    public ResponseEntity<ApiResponse<Void>> createInstructor(@RequestBody InstructorCreateRequest instructor) {
        instructorService.createInstructor(instructor);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "created instructor successfully",
                        null
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateInstructor(
            @PathVariable Long id,
            @RequestBody InstructorUpdateRequest req
    ) {
        try {
            instructorService.updateInstructor(id, req);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "update instructor successfully",
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
    public ResponseEntity<ApiResponse<Void>> deleteInstructor(@PathVariable Long id) {
        try {
            instructorService.deleteInstructorById(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "delete instructor successfully",
                            null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, e.getMessage(),null)
            );
        }
    }
}
