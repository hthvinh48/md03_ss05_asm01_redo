package com.example.asm01.service;

import com.example.asm01.dto.response.EnrollmentDetail;
import com.example.asm01.dto.response.CourseEnrollmentResponse;
import com.example.asm01.dto.response.StudentEnrollmentResponse;
import com.example.asm01.model.Course;
import com.example.asm01.model.Student;
import com.example.asm01.model.StudentEnrollment;
import com.example.asm01.repository.CourseRepository;
import com.example.asm01.repository.StudentEnrollmentRepository;
import com.example.asm01.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentEnrollmentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public StudentEnrollmentService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            StudentEnrollmentRepository studentEnrollmentRepository
    ) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
    }

    public StudentEnrollmentResponse findById(Long id){
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Enrollment with id: " + id + " not found")
        );

        return new StudentEnrollmentResponse(
                studentEnrollment.getCourse().getId(),
                studentEnrollment.getStudent().getId()
        );
    }

    public List<EnrollmentDetail> findAllStudentEnrollments() {
        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAll();
        List<EnrollmentDetail> enrollmentDetails = new ArrayList<>();

        studentEnrollments.forEach(studentEnrollment -> {
            EnrollmentDetail enrollmentDetail = new EnrollmentDetail();
            enrollmentDetail.setId(studentEnrollment.getId());
            enrollmentDetail.setStudentName(studentEnrollment.getStudent().getName());
            enrollmentDetail.setCourseName(studentEnrollment.getCourse().getTitle());
            enrollmentDetails.add(enrollmentDetail);
        });

        return enrollmentDetails;
    }

    public CourseEnrollmentResponse createStudentEnrolment(Long courseId, Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new RuntimeException("Student with id: " + studentId + " not found")
        );

        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new RuntimeException("Course with id: " + courseId + " not found")
        );

        StudentEnrollment studentEnrollment = new StudentEnrollment();
        studentEnrollment.setStudent(student);
        studentEnrollment.setCourse(course);
        studentEnrollmentRepository.save(studentEnrollment);

        return new CourseEnrollmentResponse(
                courseId,
                studentId,
                studentEnrollment.getEnrolledAt()
        );
    }

    public void updateStudentEnrolment(Long studentId, Long courseId, Long enrollmentId) {
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> new RuntimeException("Enrollment with id: " + enrollmentId + " not found")
        );

        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new RuntimeException("Course with id: " + courseId + " not found")
        );

        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new RuntimeException("Student with id: " + studentId + " not found")
        );

        studentEnrollment.setStudent(student);
        studentEnrollment.setCourse(course);
        studentEnrollmentRepository.save(studentEnrollment);
    }

    public void deleteStudentEnrolment(Long enrollmentId) {
        StudentEnrollment studentEnrollment = studentEnrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> new RuntimeException("Student with id: " + enrollmentId + " not found")
        );

        studentEnrollmentRepository.delete(studentEnrollment);
    }
}
