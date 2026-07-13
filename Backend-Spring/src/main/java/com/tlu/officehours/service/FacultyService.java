package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class FacultyService {

    @Autowired
    private FacultyProfileRepository facultyProfileRepository;

    @Autowired
    private AvailableSlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * GET /api/faculty/profile
     */
    public Map<String, Object> getProfile(User user) {
        FacultyProfile fp = facultyProfileRepository.findByIdWithDetails(user.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("faculty_user_id", fp.getFacultyUserId());
        response.put("faculty_name", fp.getFacultyName());
        response.put("department_id", fp.getDepartmentId());
        response.put("department_name", fp.getDepartment() != null ? fp.getDepartment().getDepartmentName() : null);
        response.put("phone_number", fp.getPhoneNumber());
        response.put("email", user.getEmail());
        response.put("degree", fp.getDegree());
        response.put("office_location", fp.getOfficeLocation());
        response.put("avatar_url", fp.getAvatar() != null ? baseUrl + "/storage/" + fp.getAvatar() : null);
        return response;
    }

    /**
     * PUT /api/faculty/profile (multipart)
     */
    @Transactional
    public Map<String, Object> updateProfile(User user, String facultyName, String departmentId,
                                              String degree, String phoneNumber, String officeLocation,
                                              MultipartFile avatar) {
        FacultyProfile fp = facultyProfileRepository.findByFacultyUserId(user.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        if (facultyName != null) fp.setFacultyName(facultyName);
        if (departmentId != null) fp.setDepartmentId(departmentId);
        if (degree != null) fp.setDegree(degree);
        if (phoneNumber != null) fp.setPhoneNumber(phoneNumber);
        if (officeLocation != null) fp.setOfficeLocation(officeLocation);

        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = saveAvatar(avatar);
            fp.setAvatar(avatarPath);
        }

        facultyProfileRepository.save(fp);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Cập nhật thông tin thành công.");
        response.put("faculty_user_id", fp.getFacultyUserId());
        response.put("faculty_name", fp.getFacultyName());
        response.put("department_id", fp.getDepartmentId());
        response.put("degree", fp.getDegree());
        response.put("phone_number", fp.getPhoneNumber());
        response.put("office_location", fp.getOfficeLocation());
        response.put("avatar_url", fp.getAvatar() != null ? baseUrl + "/storage/" + fp.getAvatar() : null);
        return response;
    }

    /**
     * GET /api/faculty/dashboard
     */
    public Map<String, Object> getDashboard(User user) {
        Long fid = user.getUserId();

        long totalSlots = slotRepository.countByFacultyUserId(fid);
        long availableSlots = slotRepository.countByFacultyUserIdAndIsAvailable(fid, true);
        long totalBookings = bookingRepository.countByFacultyUserId(fid);
        long pendingBookings = bookingRepository.countByFacultyAndStatus(fid, BookingStatus.PENDING);
        long confirmedBookings = bookingRepository.countByFacultyAndStatus(fid, BookingStatus.CONFIRMED);

        // Recent bookings (last 5)
        List<Booking> allBookings = bookingRepository.findByFacultyUserIdWithDetails(fid);
        List<Map<String, Object>> recentBookings = allBookings.stream()
            .limit(5)
            .map(this::toBookingResponse)
            .toList();

        // Upcoming slots (next 5 available)
        List<AvailableSlot> slots = slotRepository.findByFacultyUserId(fid);
        List<Map<String, Object>> upcomingSlots = slots.stream()
            .filter(s -> s.getIsAvailable() && s.getStartTime().isAfter(java.time.LocalDateTime.now()))
            .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
            .limit(5)
            .map(this::toSlotResponse)
            .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total_slots", totalSlots);
        response.put("available_slots", availableSlots);
        response.put("total_bookings", totalBookings);
        response.put("pending_bookings", pendingBookings);
        response.put("confirmed_bookings", confirmedBookings);
        response.put("recent_bookings", recentBookings);
        response.put("upcoming_slots", upcomingSlots);
        return response;
    }

    // ==================== Helper Methods ====================

    /**
     * Convert Booking entity to response map matching Android's expected format
     */
    public Map<String, Object> toBookingResponse(Booking b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("BookingId", b.getBookingId());
        map.put("SlotId", b.getSlotId());
        map.put("StudentUserId", b.getStudentUserId());
        map.put("BookingTime", b.getBookingTime());
        map.put("Purpose", b.getPurpose());
        map.put("Status", b.getStatus().getValue());
        map.put("CancellationTime", b.getCancellationTime());
        map.put("CancellationReason", b.getCancellationReason());
        map.put("created_at", b.getCreatedAt());
        map.put("updated_at", b.getUpdatedAt());

        // Nested slot
        if (b.getSlot() != null) {
            map.put("slot", toSlotResponse(b.getSlot()));
        }

        // Nested student with profile
        if (b.getStudent() != null) {
            Map<String, Object> studentMap = new LinkedHashMap<>();
            studentMap.put("UserId", b.getStudent().getUserId());
            studentMap.put("email", b.getStudent().getEmail());

            StudentProfile sp = b.getStudent().getStudentProfile();
            if (sp != null) {
                Map<String, Object> profileMap = new LinkedHashMap<>();
                profileMap.put("StudentUserId", sp.getStudentUserId());
                profileMap.put("StudentName", sp.getStudentName());
                profileMap.put("StudentCode", sp.getStudentCode());
                profileMap.put("ClassName", sp.getClassName());
                profileMap.put("PhoneNumber", sp.getPhoneNumber());
                profileMap.put("avatar", sp.getAvatar());
                studentMap.put("student_profile", profileMap);
            }
            map.put("student", studentMap);
        }

        return map;
    }

    public Map<String, Object> toSlotResponse(AvailableSlot s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("SlotId", s.getSlotId());
        map.put("faculty_user_id", s.getFacultyUserId());
        map.put("StartTime", s.getStartTime());
        map.put("EndTime", s.getEndTime());
        map.put("MaxStudents", s.getMaxStudents());
        map.put("IsAvailable", s.getIsAvailable());
        map.put("DefinitionId", s.getDefinitionId());
        map.put("created_at", s.getCreatedAt());
        map.put("updated_at", s.getUpdatedAt());
        return map;
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
