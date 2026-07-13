package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AvailableSlotRepository availableSlotRepository;

    @Autowired
    private FacultyProfileRepository facultyProfileRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Book an appointment
     */
    @Transactional
    public Map<String, Object> bookAppointment(User user, Long slotId, String purpose, Integer memberCount) {
        // Verify student role
        if (!user.hasRole("student")) {
            throw new BadRequestException("Unauthorized: Only students can book appointments.");
        }

        // Find slot with pessimistic write lock to handle concurrency safely
        AvailableSlot slot = availableSlotRepository.findByIdForUpdate(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        // Check availability
        if (!slot.getIsAvailable()) {
            throw new BadRequestException("Slot is not available");
        }

        // Check capacity
        int count = memberCount != null ? memberCount : 1;
        long activeBookings = bookingRepository.countActiveBookingsBySlotId(
            slotId, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );

        if (activeBookings + count > slot.getMaxStudents()) {
            throw new BadRequestException("Số lượng sinh viên vượt quá giới hạn của slot này.");
        }

        // Check duplicate booking
        if (bookingRepository.existsBySlotIdAndStudentUserId(slotId, user.getUserId())) {
            throw new BadRequestException("You have already booked this slot.");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setSlotId(slotId);
        booking.setStudentUserId(user.getUserId());
        booking.setBookingTime(LocalDateTime.now());
        booking.setPurpose(purpose);
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        // Update slot availability if maxStudents <= 1
        if (slot.getMaxStudents() <= 1) {
            slot.setIsAvailable(false);
            availableSlotRepository.save(slot);
        }

        // Create notification for faculty
        notificationService.notifyOnBookingCreation(booking, user, slot);

        // Build response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Booking successful");

        Map<String, Object> bookingMap = new LinkedHashMap<>();
        bookingMap.put("BookingId", booking.getBookingId());
        bookingMap.put("SlotId", booking.getSlotId());
        bookingMap.put("StudentUserId", booking.getStudentUserId());
        bookingMap.put("BookingTime", booking.getBookingTime());
        bookingMap.put("Purpose", booking.getPurpose());
        bookingMap.put("Status", booking.getStatus().getValue());
        response.put("booking", bookingMap);

        return response;
    }

    /**
     * Get all appointments for a student
     */
    public List<Map<String, Object>> getAppointments(User user) {
        List<Booking> bookings = bookingRepository.findByStudentUserIdWithSlotAndFaculty(user.getUserId());

        return bookings.stream().map(booking -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", booking.getBookingId());

            AvailableSlot slot = booking.getSlot();
            String teacherName = "";
            String department = "";
            String room = "";
            Long facultyUserId = null;
            String avatarUrl = null;

            if (slot != null) {
                facultyUserId = slot.getFacultyUserId();

                // Get faculty profile
                FacultyProfile fp = facultyProfileRepository.findByIdWithDetails(slot.getFacultyUserId())
                    .orElse(null);

                if (fp != null) {
                    String degree = fp.getDegree() != null ? fp.getDegree() + ". " : "";
                    teacherName = degree + fp.getFacultyName();
                    department = fp.getDepartment() != null ? fp.getDepartment().getDepartmentName() : "";
                    room = fp.getOfficeLocation() != null ? fp.getOfficeLocation() : "";
                    avatarUrl = fp.getAvatar() != null ? baseUrl + "/storage/" + fp.getAvatar() : null;
                }

                map.put("time", slot.getStartTime().format(TIME_FORMAT) + " - " + slot.getEndTime().format(TIME_FORMAT));
                map.put("date", slot.getStartTime().format(DATE_FORMAT));
            } else {
                map.put("time", "");
                map.put("date", "");
            }

            map.put("teacherName", teacherName);
            map.put("department", department);
            map.put("room", room);
            map.put("purpose", booking.getPurpose() != null ? booking.getPurpose() : "");
            map.put("status", booking.getStatus().getValue());
            map.put("cancellationReason", booking.getCancellationReason() != null ? booking.getCancellationReason() : "");
            map.put("faculty_user_id", facultyUserId);
            map.put("avatarUrl", avatarUrl);

            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Cancel an appointment
     */
    @Transactional
    public Map<String, Object> cancelAppointment(User user, Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify ownership
        if (!booking.getStudentUserId().equals(user.getUserId())) {
            throw new BadRequestException("Unauthorized: This is not your booking.");
        }

        // Check if can be cancelled
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Chỉ có thể hủy booking đang ở trạng thái pending hoặc confirmed.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationTime(LocalDateTime.now());
        booking.setCancellationReason(reason);
        bookingRepository.save(booking);

        // Restore slot availability
        AvailableSlot slot = availableSlotRepository.findById(booking.getSlotId()).orElse(null);
        if (slot != null && !slot.getIsAvailable()) {
            slot.setIsAvailable(true);
            availableSlotRepository.save(slot);
        }

        // Notify faculty
        notificationService.notifyOnBookingCancellation(booking, user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Hủy lịch hẹn thành công.");
        return response;
    }
}
