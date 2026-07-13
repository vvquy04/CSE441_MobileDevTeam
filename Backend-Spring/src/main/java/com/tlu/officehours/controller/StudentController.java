package com.tlu.officehours.controller;

import com.tlu.officehours.entity.User;
import com.tlu.officehours.security.CustomUserDetailsService;
import com.tlu.officehours.service.BookingService;
import com.tlu.officehours.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * GET /api/student/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(studentService.getProfile(user));
    }

    /**
     * PUT /api/student/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> profileData
    ) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(studentService.updateProfile(user, profileData));
    }

    /**
     * GET /api/student/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(studentService.getDashboard(user));
    }

    /**
     * POST /api/student/book-appointment
     */
    @PostMapping("/book-appointment")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());

        Long slotId = Long.valueOf(request.get("SlotId").toString());
        String purpose = request.get("Purpose") != null ? request.get("Purpose").toString() : null;
        Integer memberCount = request.get("MemberCount") != null
            ? Integer.valueOf(request.get("MemberCount").toString()) : 1;

        return ResponseEntity.ok(bookingService.bookAppointment(user, slotId, purpose, memberCount));
    }

    /**
     * GET /api/student/appointments
     */
    @GetMapping("/appointments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        List<Map<String, Object>> appointments = bookingService.getAppointments(user);
        return ResponseEntity.ok(appointments);
    }

    /**
     * POST /api/student/appointments/{id}/cancel
     */
    @PostMapping("/appointments/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancelAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(bookingService.cancelAppointment(user, id, reason));
    }
}
