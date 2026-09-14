package com.example.asm01.service;

import com.example.asm01.dto.request.CourseCreateRequest;
import com.example.asm01.dto.request.CourseDropoutRequest;
import com.example.asm01.dto.request.CourseEnrollmentRequest;
import com.example.asm01.dto.request.CourseUpdateRequest;
import com.example.asm01.dto.response.*;
import com.example.asm01.model.*;
import com.example.asm01.repository.CourseRepository;
import com.example.asm01.repository.InstructorRepository;
import com.example.asm01.repository.StudentEnrollmentRepository;
import com.example.asm01.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentEnrollmentService studentEnrollmentService;
    private final StudentRepository studentRepository;

    public CourseService(
            CourseRepository courseRepository,
            InstructorRepository instructorRepository,
            StudentEnrollmentRepository studentEnrollmentRepository,
            StudentEnrollmentService studentEnrollmentService,
            StudentRepository studentRepository
    ) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
        this.studentEnrollmentService = studentEnrollmentService;
        this.studentRepository = studentRepository;
    }

    private CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getStatus(),
                new CourseInstructorResponse(
                        course.getInstructor().getId(),
                        course.getInstructor().getName()
                )
        );
    }

    public PageResponse<CourseResponse> getPagedCourses(
            int page, int size, String sortBy, Sort.Direction direction
    ) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (sortBy == null || sortBy.isBlank()) sortBy = "id";
        if (direction == null) direction = Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<CourseResponse> coursePage= courseRepository.findAll(pageable).map(this::toCourseResponse);

        return new PageResponse<>(
                coursePage.getContent(),
                coursePage.getNumber(),
                coursePage.getSize(),
                (int) coursePage.getTotalElements(),
                coursePage.getTotalPages(),
                coursePage.isLast()
        );
    }

    public CourseResponse findCourseById(Long id) {
        Course existing = courseRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Course with id " + id + " not found!")
        );

        return toCourseResponse(existing);
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

    public CourseEnrollmentResponse enrollCourse(Long courseId, CourseEnrollmentRequest req) {
        boolean isExistActiveCourse = courseRepository.existsByIdAndStatus(courseId, CourseStatus.ACTIVE);
        boolean isExistEnrollment = studentEnrollmentRepository.existsByCourseIdAndStudentId(courseId, req.getStudentId());

        if (!isExistActiveCourse) {
            throw new RuntimeException("Course with id " + courseId + " not found!");
        } else if (isExistEnrollment) {
            throw new RuntimeException("Student already enrolled this course!");
        }

        return studentEnrollmentService.createStudentEnrolment(courseId, req.getStudentId());
    }

    @Transactional
    public void deleteStudentEnrollment(Long courseId, CourseDropoutRequest request) {
        boolean isExistActiveCourse = courseRepository.existsByIdAndStatus(courseId, CourseStatus.ACTIVE);

        Student student = studentRepository.findById(request.getStudentId()).orElseThrow(
                () -> new RuntimeException("Student with id " + request.getStudentId() + " not found!")
        );

        boolean isExistEnrollment = studentEnrollmentRepository.existsByCourseIdAndStudentId(courseId, request.getStudentId());

        if (!isExistActiveCourse) {
            throw new RuntimeException("Course with id " + courseId + " not found!");
        }

        if (isExistEnrollment) {
            studentEnrollmentRepository.deleteByCourseIdAndStudentId(courseId, request.getStudentId());
        }
    }

    public List<StudentResponse> getStudentsByCourseId(Long courseId, String search) {
        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findByCourseId(courseId);

        List<Long> studentIds = new ArrayList<>();

        studentEnrollments.forEach(enrollment -> {
            studentIds.add(enrollment.getStudent().getId());
        });

        List<Student> students = studentRepository.findByNameContains(search);

        return students.stream()
                .filter(student -> studentIds.contains(student.getId()))
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getName(),
                        student.getEmail()
                ))
                .toList();
    }
}
