package com.tlu.officehours.controller;

import com.tlu.officehours.entity.User;
import com.tlu.officehours.security.CustomUserDetailsService;
import com.tlu.officehours.service.FacultyBookingService;
import com.tlu.officehours.service.FacultyService;
import com.tlu.officehours.service.FacultySlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private FacultyBookingService bookingService;

    @Autowired
    private FacultySlotService slotService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // ==================== PROFILE ====================

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(facultyService.getProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "faculty_name", required = false) String facultyName,
            @RequestParam(value = "department_id", required = false) String departmentId,
            @RequestParam(value = "degree", required = false) String degree,
            @RequestParam(value = "phone_number", required = false) String phoneNumber,
            @RequestParam(value = "office_location", required = false) String officeLocation,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(
            facultyService.updateProfile(user, facultyName, departmentId, degree, phoneNumber, officeLocation, avatar)
        );
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(facultyService.getDashboard(user));
    }

    // ==================== BOOKINGS ====================

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getAllBookings(user));
    }

    @GetMapping("/bookings/pending")
    public ResponseEntity<?> getPendingBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getPendingBookings(user));
    }

    @GetMapping("/bookings/confirmed")
    public ResponseEntity<?> getConfirmedBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getConfirmedBookings(user));
    }

    @GetMapping("/bookings/by-date")
    public ResponseEntity<?> getBookingsByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("date") String date,
            @RequestParam(value = "status", required = false) String status
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getBookingsByDate(user, date, status));
    }

    @GetMapping("/bookings/by-week")
    public ResponseEntity<?> getBookingsByWeek(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam(value = "status", required = false) String status
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getBookingsByWeek(user, startDate, endDate, status));
    }

    @GetMapping("/bookings/by-status")
    public ResponseEntity<?> getBookingsByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("status") String status,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.getBookingsByStatus(user, status, limit));
    }

    @PostMapping("/bookings/{id}/approve")
    public ResponseEntity<?> approveBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.approveBooking(user, id));
    }

    @PostMapping("/bookings/{id}/reject")
    public ResponseEntity<?> rejectBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.rejectBooking(user, id, body.get("reason")));
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.cancelBooking(user, id, body.get("reason")));
    }

    @PostMapping("/bookings/{id}/complete")
    public ResponseEntity<?> markCompleted(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(bookingService.markCompleted(user, id));
    }

    // ==================== SLOTS ====================

    @GetMapping("/slots")
    public ResponseEntity<?> getSlots(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(slotService.getAllSlots(user));
    }

    @PostMapping("/slots")
    public ResponseEntity<?> createSlot(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.status(201).body(slotService.createSlot(user, request));
    }

    @PostMapping("/slots/multiple")
    public ResponseEntity<?> createMultipleSlots(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.status(201).body(slotService.createMultipleSlots(user, request));
    }

    @PostMapping("/slots/monthly")
    public ResponseEntity<?> createMonthlySchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.status(201).body(slotService.createMonthlySchedule(user, request));
    }

    @PutMapping("/slots/{id}")
    public ResponseEntity<?> updateSlot(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(slotService.updateSlot(user, id, request));
    }

    @DeleteMapping("/slots/{id}")
    public ResponseEntity<?> deleteSlot(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(slotService.deleteSlot(user, id));
    }

    @PostMapping("/slots/{id}/toggle")
    public ResponseEntity<?> toggleSlotAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(slotService.toggleAvailability(user, id));
    }

    // ==================== Helper ====================

    private User getUser(UserDetails userDetails) {
        return userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
    }
}
