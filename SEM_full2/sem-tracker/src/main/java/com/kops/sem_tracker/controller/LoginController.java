package com.kops.sem_tracker.controller;

import com.kops.sem_tracker.entyties.Student;
import com.kops.sem_tracker.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                return createErrorResponse("Username is required", HttpStatus.BAD_REQUEST);
            }
            if (password == null || password.trim().isEmpty()) {
                return createErrorResponse("Password is required", HttpStatus.BAD_REQUEST);
            }

            System.out.println("Login attempt for username: " + username);

            // Check if user exists (try both username and email)
            Optional<Student> studentByUsername = studentService.findByUsername(username.trim());
            Optional<Student> studentByEmail = studentService.findByEmail(username.trim());

            Student student = null;
            if (studentByUsername.isPresent()) {
                student = studentByUsername.get();
            } else if (studentByEmail.isPresent()) {
                student = studentByEmail.get();
            }

            if (student == null) {
                return createErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }

            // Verify password
            if (!passwordEncoder.matches(password, student.getPassword())) {
                return createErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }

            System.out.println("Login successful for user: " + student.getUsername());

            // Create success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", Map.of(
                    "id", student.getId(),
                    "username", student.getUsername(),
                    "email", student.getEmail()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("An error occurred during login. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/check-credentials")
    public ResponseEntity<?> checkCredentials(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of("exists", false));
            }

            // Check if username or email exists
            boolean usernameExists = studentService.checkUsernameExists(username.trim());
            boolean emailExists = studentService.checkEmailExists(username.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("exists", usernameExists || emailExists);
            response.put("isEmail", emailExists);
            response.put("isUsername", usernameExists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check credentials"));
        }
    }

    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}