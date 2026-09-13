package com.example.asm01.service;

import com.example.asm01.dto.request.CourseCreateRequest;
import com.example.asm01.dto.request.CourseUpdateRequest;
import com.example.asm01.dto.response.CourseInstructorResponse;
import com.example.asm01.model.Course;
import com.example.asm01.model.Instructor;
import com.example.asm01.repository.CourseRepository;
import com.example.asm01.repository.InstructorRepository;
import com.example.asm01.dto.response.CourseResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public CourseService(
            CourseRepository courseRepository,
            InstructorRepository instructorRepository
    ) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<CourseResponse> findAllCourses() {
        return courseRepository.findAll().stream().map(
                course -> new CourseResponse(
                        course.getId(),
                        course.getTitle(),
                        course.getStatus(),
                        new CourseInstructorResponse(
                                course.getInstructor().getId(),
                                course.getInstructor().getName()
                        )
                )
        ).toList();
    }

    public CourseResponse findCourseById(Long id) {
        Course existing = courseRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Course with id " + id + " not found!")
        );

        return new CourseResponse(
                existing.getId(),
                existing.getTitle(),
                existing.getStatus(),
                new CourseInstructorResponse(
                        existing.getInstructor().getId(),
                        existing.getInstructor().getName()
                )
        );
    }

    public void createCourse(CourseCreateRequest req) {
        Course newCourse = new Course();
        Instructor instructor = instructorRepository.findById(req.getInstructorId()).orElseThrow(
                () -> new RuntimeException("Instructor " + req.getInstructorId() + " not found!")
        );

        newCourse.setTitle(req.getTitle());
        newCourse.setStatus(req.getStatus());
        newCourse.setInstructor(instructor);
        courseRepository.save(newCourse);
    }

    public void updateCourse(Long id, CourseUpdateRequest req) {
        Course existingCourse = courseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Course with id " + id + " not found!")
        );

        Instructor existingInstructor = Objects.equals(
                existingCourse.getInstructor().getId(), req.getInstructorId()) ?
                existingCourse.getInstructor() : instructorRepository.findById(req.getInstructorId()).orElseThrow(
                        () -> new RuntimeException("Instructor " + req.getInstructorId() + " not found!")
                );

        existingCourse.setTitle(req.getTitle());
        existingCourse.setStatus(req.getStatus());
        existingCourse.setInstructor(existingInstructor);
        courseRepository.save(existingCourse);
    }

    public void deleteCourseById(Long id) {
        Course existingCourse = courseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Course with id " + id + " not found!")
        );

        courseRepository.delete(existingCourse);
    }
}
