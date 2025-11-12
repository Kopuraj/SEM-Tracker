package com.kops.sem_tracker.service;

import com.kops.sem_tracker.entyties.Student;
import com.kops.sem_tracker.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student registerStudent(Student student) {
        try {
            // Trim whitespace from inputs
            student.setUsername(student.getUsername().trim());
            student.setEmail(student.getEmail().toLowerCase().trim());

            // Check if email exists
            Optional<Student> existingByEmail = studentRepository.findByEmail(student.getEmail());
            if (existingByEmail.isPresent()) {
                throw new RuntimeException("Email already registered");
            }

            // Check if username exists
            Optional<Student> existingByUsername = studentRepository.findByUsername(student.getUsername());
            if (existingByUsername.isPresent()) {
                throw new RuntimeException("Username already taken");
            }

            // Encode password before saving
            student.setPassword(passwordEncoder.encode(student.getPassword()));

            // Save student to database
            Student savedStudent = studentRepository.save(student);
            System.out.println("Student saved to database with ID: " + savedStudent.getId());

            return savedStudent;
        } catch (RuntimeException e) {
            // Re-throw runtime exceptions (validation errors)
            throw e;
        } catch (Exception e) {
            // Handle any database or other unexpected errors
            System.err.println("Error saving student to database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to register student. Please try again.");
        }
    }

    public List<Student> getAllStudents() {
        try {
            return studentRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching students: " + e.getMessage());
            throw new RuntimeException("Failed to fetch students");
        }
    }

    public boolean checkUsernameExists(String username) {
        try {
            return studentRepository.findByUsername(username.trim()).isPresent();
        } catch (Exception e) {
            System.err.println("Error checking username: " + e.getMessage());
            throw new RuntimeException("Failed to check username availability");
        }
    }

    public boolean checkEmailExists(String email) {
        try {
            return studentRepository.findByEmail(email.toLowerCase().trim()).isPresent();
        } catch (Exception e) {
            System.err.println("Error checking email: " + e.getMessage());
            throw new RuntimeException("Failed to check email availability");
        }
    }

    public Optional<Student> findByEmail(String email) {
        try {
            return studentRepository.findByEmail(email.toLowerCase().trim());
        } catch (Exception e) {
            System.err.println("Error finding student by email: " + e.getMessage());
            throw new RuntimeException("Failed to find student");
        }
    }

    public Optional<Student> findByUsername(String username) {
        try {
            return studentRepository.findByUsername(username.trim());
        } catch (Exception e) {
            System.err.println("Error finding student by username: " + e.getMessage());
            throw new RuntimeException("Failed to find student");
        }
    }
}