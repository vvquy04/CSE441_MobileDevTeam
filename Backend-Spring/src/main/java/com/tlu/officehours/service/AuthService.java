package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.repository.*;
import com.tlu.officehours.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private FacultyProfileRepository facultyProfileRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Login - authenticate and return JWT token
     */
    public Map<String, Object> login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));

        List<String> roles = user.getRoles().stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", token);
        response.put("token_type", "Bearer");

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        userMap.put("email_verified_at", user.getEmailVerifiedAt());
        userMap.put("created_at", user.getCreatedAt());
        userMap.put("updated_at", user.getUpdatedAt());
        response.put("user", userMap);
        response.put("roles", roles);

        return response;
    }

    /**
     * Register a new student
     */
    @Transactional
    public Map<String, Object> registerStudent(String email, String password,
                                                String studentName, String studentCode,
                                                String className, String phoneNumber,
                                                MultipartFile avatar) {
        // Validate email domain
        if (!email.endsWith("@e.tlu.edu.vn")) {
            throw new BadRequestException("Email sinh viên phải có đuôi @e.tlu.edu.vn");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã được sử dụng.");
        }

        if (studentProfileRepository.existsByStudentCode(studentCode)) {
            throw new BadRequestException("Mã sinh viên đã tồn tại.");
        }

        // Create user
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);

        // Assign student role
        Role studentRole = roleRepository.findByRoleName("student")
            .orElseThrow(() -> new BadRequestException("Role student not found"));
        user.getRoles().add(studentRole);
        user = userRepository.save(user);

        // Handle avatar upload
        String avatarPath = null;
        if (avatar != null && !avatar.isEmpty()) {
            avatarPath = saveAvatar(avatar);
        }

        // Create student profile
        StudentProfile profile = new StudentProfile();
        profile.setStudentUserId(user.getUserId());
        profile.setStudentName(studentName);
        profile.setStudentCode(studentCode);
        profile.setClassName(className);
        profile.setPhoneNumber(phoneNumber);
        profile.setAvatar(avatarPath);
        studentProfileRepository.save(profile);

        // Generate token
        String token = jwtTokenProvider.generateTokenFromEmail(email);

        // Build response matching Laravel format
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng ký sinh viên thành công.");
        response.put("access_token", token);
        response.put("token_type", "Bearer");

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("role", "student");
        response.put("avatar_url", avatarPath != null ? baseUrl + "/storage/" + avatarPath : null);

        return response;
    }

    /**
     * Register a new faculty member
     */
    @Transactional
    public Map<String, Object> registerFaculty(String email, String password,
                                                String facultyName, String departmentId,
                                                String degree, String phoneNumber,
                                                String officeRoom, MultipartFile avatar) {
        if (!email.endsWith("@tlu.edu.vn")) {
            throw new BadRequestException("Email giảng viên phải có đuôi @tlu.edu.vn");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email đã được sử dụng.");
        }

        // Create user
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);

        // Assign faculty role
        Role facultyRole = roleRepository.findByRoleName("faculty")
            .orElseThrow(() -> new BadRequestException("Role faculty not found"));
        user.getRoles().add(facultyRole);
        user = userRepository.save(user);

        // Handle avatar
        String avatarPath = null;
        if (avatar != null && !avatar.isEmpty()) {
            avatarPath = saveAvatar(avatar);
        }

        // Create faculty profile
        FacultyProfile profile = new FacultyProfile();
        profile.setFacultyUserId(user.getUserId());
        profile.setFacultyName(facultyName);
        profile.setDepartmentId(departmentId);
        profile.setDegree(degree);
        profile.setPhoneNumber(phoneNumber);
        profile.setOfficeLocation(officeRoom);
        profile.setAvatar(avatarPath);
        facultyProfileRepository.save(profile);

        String token = jwtTokenProvider.generateTokenFromEmail(email);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng ký giảng viên thành công.");
        response.put("access_token", token);
        response.put("token_type", "Bearer");

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("role", "faculty");
        response.put("avatar_url", avatarPath != null ? baseUrl + "/storage/" + avatarPath : null);

        return response;
    }

    /**
     * Get user profile with role-specific data
     */
    public Map<String, Object> getUserProfile(User user) {
        List<String> roles = user.getRoles().stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("roles", roles);

        // Add profile based on role
        if (user.hasRole("student")) {
            StudentProfile profile = studentProfileRepository.findByStudentUserId(user.getUserId())
                .orElse(null);
            response.put("profile", profile);
            response.put("avatar_url", profile != null && profile.getAvatar() != null
                ? baseUrl + "/storage/" + profile.getAvatar() : null);
        } else if (user.hasRole("faculty")) {
            FacultyProfile profile = facultyProfileRepository.findByFacultyUserId(user.getUserId())
                .orElse(null);
            response.put("profile", profile);
            response.put("avatar_url", profile != null && profile.getAvatar() != null
                ? baseUrl + "/storage/" + profile.getAvatar() : null);
        }

        return response;
    }

    private String saveAvatar(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path avatarDir = Paths.get(uploadDir, "avatars").toAbsolutePath().normalize();
            Files.createDirectories(avatarDir);
            Path targetPath = avatarDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "avatars/" + filename;
        } catch (IOException e) {
            throw new BadRequestException("Không thể upload avatar: " + e.getMessage());
        }
    }
}
