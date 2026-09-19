package com.example.asm01.service;

import com.example.asm01.dto.request.StudentCreateRequest;
import com.example.asm01.dto.response.PageResponse;
import com.example.asm01.dto.response.StudentResponse;
import com.example.asm01.model.Student;
import com.example.asm01.repository.StudentEnrollmentRepository;
import com.example.asm01.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PageResponse<StudentResponse> getAllStudentsPage(
            int page,
            int size,
            String name,
            String email,
            String sortBy,
            Sort.Direction direction
    ) {
        Pageable pageable;

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (name == null) name = "";
        if (email == null) email = "";
        if (sortBy == null || direction == null) {
            pageable = PageRequest.of(page, size);
        } else {
            pageable = PageRequest.of(page, size, direction, sortBy);
        }

        Page<StudentResponse> responsePage = studentRepository.findAllByNameOrEmail(name, email, pageable);

        return new PageResponse<>(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                (int) responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isLast()
        );
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
