package com.tlu.officehours.controller;

import com.tlu.officehours.entity.User;
import com.tlu.officehours.security.CustomUserDetailsService;
import com.tlu.officehours.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * GET /api/notifications
     */
    @GetMapping
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.loadUserEntityByEmail(userDetails.getUsername());
        List<Map<String, Object>> notifications = notificationService.getNotifications(user.getUserId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * POST /api/notifications/{id}/read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Long id) {
        Map<String, Object> response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }
}
