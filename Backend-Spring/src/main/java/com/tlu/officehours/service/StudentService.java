package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Get student profile
     */
    public Map<String, Object> getProfile(User user) {
        List<String> roles = user.getRoles().stream()
            .map(Role::getRoleName)
            .toList();

        StudentProfile profile = studentProfileRepository.findByStudentUserId(user.getUserId())
            .orElse(null);

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("roles", roles);

        if (profile != null) {
            Map<String, Object> profileMap = new LinkedHashMap<>();
            profileMap.put("StudentUserId", profile.getStudentUserId());
            profileMap.put("StudentName", profile.getStudentName());
            profileMap.put("StudentCode", profile.getStudentCode());
            profileMap.put("ClassName", profile.getClassName());
            profileMap.put("PhoneNumber", profile.getPhoneNumber());
            profileMap.put("avatar", profile.getAvatar());
            response.put("profile", profileMap);
            response.put("avatar_url", profile.getAvatar() != null
                ? baseUrl + "/storage/" + profile.getAvatar() : null);
        } else {
            response.put("profile", null);
            response.put("avatar_url", null);
        }

        return response;
    }

    /**
     * Update student profile
     */
    @Transactional
    public Map<String, Object> updateProfile(User user, Map<String, String> profileData) {
        StudentProfile profile = studentProfileRepository.findByStudentUserId(user.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (profileData.containsKey("StudentName") && profileData.get("StudentName") != null) {
            profile.setStudentName(profileData.get("StudentName"));
        }
        if (profileData.containsKey("StudentCode") && profileData.get("StudentCode") != null) {
            // Check uniqueness
            String newCode = profileData.get("StudentCode");
            if (!newCode.equals(profile.getStudentCode()) &&
                studentProfileRepository.existsByStudentCode(newCode)) {
                throw new BadRequestException("Mã sinh viên đã tồn tại.");
            }
            profile.setStudentCode(newCode);
        }
        if (profileData.containsKey("ClassName")) {
            profile.setClassName(profileData.get("ClassName"));
        }
        if (profileData.containsKey("PhoneNumber")) {
            profile.setPhoneNumber(profileData.get("PhoneNumber"));
        }

        studentProfileRepository.save(profile);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Cập nhật thông tin thành công.");

        Map<String, Object> profileMap = new LinkedHashMap<>();
        profileMap.put("StudentUserId", profile.getStudentUserId());
        profileMap.put("StudentName", profile.getStudentName());
        profileMap.put("StudentCode", profile.getStudentCode());
        profileMap.put("ClassName", profile.getClassName());
        profileMap.put("PhoneNumber", profile.getPhoneNumber());
        response.put("profile", profileMap);
        response.put("avatar_url", profile.getAvatar() != null
            ? baseUrl + "/storage/" + profile.getAvatar() : null);

        return response;
    }

    /**
     * Get student dashboard data
     */
    public Map<String, Object> getDashboard(User user) {
        StudentProfile profile = studentProfileRepository.findByStudentUserId(user.getUserId())
            .orElse(null);

        long totalBookings = bookingRepository.countByStudentUserId(user.getUserId());
        long upcomingBookings = bookingRepository.countUpcomingByStudentUserId(
            user.getUserId(),
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("UserId", user.getUserId());
        userMap.put("email", user.getEmail());
        response.put("user", userMap);
        response.put("profile", profile);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total_bookings", totalBookings);
        stats.put("upcoming_bookings", upcomingBookings);
        response.put("stats", stats);

        return response;
    }
}
