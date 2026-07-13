package com.tlu.officehours.controller;

import com.tlu.officehours.entity.User;
import com.tlu.officehours.security.CustomUserDetailsService;
import com.tlu.officehours.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * POST /api/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email và mật khẩu là bắt buộc."));
        }

        Map<String, Object> response = authService.login(email, password);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register-student
     * Multipart form-data (matching Android app's request format)
     */
    @PostMapping("/auth/register-student")
    public ResponseEntity<?> registerStudent(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("password_confirmation") String passwordConfirmation,
            @RequestParam("StudentName") String studentName,
            @RequestParam("StudentCode") String studentCode,
            @RequestParam(value = "ClassName", required = false) String className,
            @RequestParam(value = "PhoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        // Validate password match
        if (!password.equals(passwordConfirmation)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu xác nhận không khớp."));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự."));
        }

        Map<String, Object> response = authService.registerStudent(
            email, password, studentName, studentCode, className, phoneNumber, avatar
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register-faculty
     * Multipart form-data (matching Android app's register-faculty API call)
     */
    @PostMapping("/auth/register-faculty")
    public ResponseEntity<?> registerFaculty(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("password_confirmation") String passwordConfirmation,
            @RequestParam("faculty_name") String facultyName,
            @RequestParam("department_id") String departmentId,
            @RequestParam(value = "degree", required = false) String degree,
            @RequestParam(value = "phone_number", required = false) String phoneNumber,
            @RequestParam(value = "office_room", required = false) String officeRoom,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        if (!password.equals(passwordConfirmation)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu xác nhận không khớp."));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự."));
        }

        Map<String, Object> response = authService.registerFaculty(
            email, password, facultyName, departmentId, degree, phoneNumber, officeRoom, avatar
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT is stateless - client removes the token
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công."));
    }

    /**
     * GET /api/user/profile
     */
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        Map<String, Object> response = authService.getUserProfile(user);
        return ResponseEntity.ok(response);
    }
}
