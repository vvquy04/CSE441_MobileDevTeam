package com.tlu.officehours.service;

import com.tlu.officehours.entity.FacultyProfile;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.FacultyProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private FacultyProfileRepository facultyProfileRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Get top 5 featured teachers
     */
    public List<Map<String, Object>> getFeaturedTeachers() {
        List<FacultyProfile> profiles = facultyProfileRepository.findAllWithDepartmentAndUser();
        return profiles.stream()
            .limit(5)
            .map(this::toTeacherResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get teachers by department
     */
    public List<Map<String, Object>> getTeachersByDepartment(String departmentId) {
        List<FacultyProfile> profiles = facultyProfileRepository.findByDepartmentIdWithDetails(departmentId);
        return profiles.stream()
            .map(this::toTeacherResponse)
            .collect(Collectors.toList());
    }

    /**
     * Search teachers by name
     */
    public List<Map<String, Object>> searchTeachers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getFeaturedTeachers();
        }
        List<FacultyProfile> profiles = facultyProfileRepository.searchByName(query.trim());
        return profiles.stream()
            .map(this::toTeacherResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get teacher detail by faculty user ID
     */
    public Map<String, Object> getTeacherDetail(Long facultyUserId) {
        FacultyProfile profile = facultyProfileRepository.findByIdWithDetails(facultyUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên."));
        return toTeacherResponse(profile);
    }

    /**
     * Convert FacultyProfile to response map matching Laravel's format
     */
    private Map<String, Object> toTeacherResponse(FacultyProfile fp) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("faculty_user_id", fp.getFacultyUserId());
        map.put("faculty_name", fp.getFacultyName());
        map.put("degree", fp.getDegree());
        map.put("phone_number", fp.getPhoneNumber());
        map.put("office_location", fp.getOfficeLocation());
        map.put("avatar", fp.getAvatar());
        map.put("department_name", fp.getDepartment() != null ? fp.getDepartment().getDepartmentName() : null);
        map.put("email", fp.getUser() != null ? fp.getUser().getEmail() : null);
        map.put("avatar_url", fp.getAvatar() != null ? baseUrl + "/storage/" + fp.getAvatar() : null);
        return map;
    }
}
