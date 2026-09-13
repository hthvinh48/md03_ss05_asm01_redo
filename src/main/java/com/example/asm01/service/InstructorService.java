package com.example.asm01.service;

import com.example.asm01.dto.request.InstructorCreateRequest;
import com.example.asm01.dto.InstructorDetail;
import com.example.asm01.dto.request.InstructorUpdateRequest;
import com.example.asm01.dto.response.CourseInstructorResponse;
import com.example.asm01.dto.response.CourseResponse;
import com.example.asm01.model.Course;
import com.example.asm01.model.CourseStatus;
import com.example.asm01.model.StudentEnrollment;
import com.example.asm01.model.Instructor;
import com.example.asm01.repository.CourseRepository;
import com.example.asm01.repository.StudentEnrollmentRepository;
import com.example.asm01.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public InstructorService(
            InstructorRepository instructorRepository,
            CourseRepository courseRepository,
            StudentEnrollmentRepository studentEnrollmentRepository
    ) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
    }

    private List<CourseResponse> getCoursesByInstructor(Instructor instructor) {
        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAll();
        List<Course> courses = courseRepository.findAll();

        List<Course> validCourse = courses.stream()
                .filter(course ->
                        Objects.equals(
                                course.getInstructor().getId(),
                                instructor.getId()
                        )
                )
                .filter(course -> CourseStatus.ACTIVE.equals(course.getStatus()))
                .filter(course -> studentEnrollments.stream()
                        .anyMatch(studentEnrollment ->
                                Objects.equals(
                                        course.getId(),
                                        studentEnrollment.getCourse().getId()
                                )
                        )
                ).toList();

        return validCourse.stream().map(course ->
                new CourseResponse(
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

    public InstructorDetail findInstructorById(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Instructor with id " + id + " not found")
        );

        List<CourseResponse> courseResponses = getCoursesByInstructor(instructor);

        return new InstructorDetail(
                instructor.getId(),
                instructor.getName(),
                instructor.getEmail(),
                courseResponses
        );
    }

    public List<InstructorDetail> getInstructorDetail() {
        List<Instructor> instructors = instructorRepository.findAll();

        return instructors.stream().map(
                instructor -> {
                    List<CourseResponse> courseResponses = getCoursesByInstructor(instructor);

                    return new InstructorDetail(
                            instructor.getId(),
                            instructor.getName(),
                            instructor.getEmail(),
                            courseResponses
                    );
                }
        ).toList();
    }

    public void createInstructor(InstructorCreateRequest req) {
        Instructor instructor = new Instructor();
        instructor.setName(req.getName());
        instructor.setEmail(req.getEmail());
        instructorRepository.save(instructor);
    }

    public void updateInstructor(Long id, InstructorUpdateRequest req) {
        Instructor existingInstructor = instructorRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Instructor with id " + id + " not found")
        );

        List<Course> listOfCourses = new ArrayList<>();

        req.getCourseIds().forEach(courseId -> {

            Course course = courseRepository.findById(courseId).orElseThrow(
                    () -> new RuntimeException("Course with id " + courseId + " not found")
            );

            course.setInstructor(existingInstructor);
            listOfCourses.add(course);
        });

        existingInstructor.setName(req.getName());
        existingInstructor.setEmail(req.getEmail());
        existingInstructor.setCourses(listOfCourses);

        instructorRepository.save(existingInstructor);
        courseRepository.saveAll(listOfCourses);
    }

    public void deleteInstructorById(Long id) {
        Instructor existingInstructor = instructorRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Instructor with id " + id + " not found")
        );

        List<Long> courseIds = existingInstructor.getCourses().stream().mapToLong(Course::getId).boxed().toList();

        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAll().stream()
                .filter(studentEnrollment -> courseIds.stream().anyMatch(courseId ->
                        Objects.equals(
                                courseId,
                                studentEnrollment.getCourse().getId()
                        )
                )).toList();

        studentEnrollmentRepository.deleteAll(studentEnrollments);
        courseRepository.deleteAll(existingInstructor.getCourses());
        instructorRepository.delete(existingInstructor);
    }
}
