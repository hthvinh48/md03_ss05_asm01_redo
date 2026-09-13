package com.example.asm01.service;

import com.example.asm01.dto.request.StudentCreateRequest;
import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.model.Student;
import com.example.asm01.repository.StudentEnrollmentRepository;
import com.example.asm01.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public StudentService(StudentRepository studentRepository, StudentEnrollmentRepository studentEnrollmentRepository) {
        this.studentRepository = studentRepository;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(student -> {
            StudentResponse studentResponse = new StudentResponse();
            studentResponse.setId(student.getId());
            studentResponse.setName(student.getName());
            studentResponse.setEmail(student.getEmail());
            return studentResponse;
        }).toList();
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("the student with id " + id + " not found.")
        );

        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail()
        );
    }

    public void createStudent(StudentCreateRequest req) {
        Student student = new Student();
        student.setName(req.getName());
        student.setEmail(req.getEmail());
        studentRepository.save(student);
    }

    public void updateStudent(Long id, StudentCreateRequest req) {
        Student existingStudent = studentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("the student with id " + id + " not found.")
        );

        existingStudent.setName(req.getName());
        existingStudent.setEmail(req.getEmail());
        studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("the student with id " + id + " not found.")
        );

        student.getEnrollments().forEach(enrollment -> {
            if (Objects.equals(enrollment.getStudent().getId(), student.getId())) {
                studentEnrollmentRepository.delete(enrollment);
            }
        });
        studentRepository.deleteById(id);
    }
}
