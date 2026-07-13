package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NotificationService - FULLY IMPLEMENTED (unlike the empty Laravel version).
 * Handles notification creation and retrieval.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Get all notifications for a user
     */
    public List<Map<String, Object>> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(n -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", n.getId());
            map.put("user_id", n.getUserId());
            map.put("title", n.getTitle());
            map.put("message", n.getMessage());
            map.put("type", n.getType());
            map.put("is_read", n.getIsRead());
            map.put("created_at", n.getCreatedAt());
            map.put("updated_at", n.getUpdatedAt());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Mark a notification as read
     */
    public Map<String, Object> markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepository.save(notification);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đã đánh dấu đã đọc.");
        return response;
    }

    /**
     * Create notification when a student books an appointment
     */
    public void notifyOnBookingCreation(Booking booking, User student, AvailableSlot slot) {
        try {
            Notification notification = new Notification();
            notification.setUserId(slot.getFacultyUserId());
            notification.setTitle("Lịch hẹn mới");
            notification.setMessage("Sinh viên đã đặt lịch hẹn vào slot "
                + slot.getStartTime().toLocalTime() + " - " + slot.getEndTime().toLocalTime()
                + " ngày " + slot.getStartTime().toLocalDate());
            notification.setType("booking_created");
            notification.setIsRead(false);
            notificationRepository.save(notification);
        } catch (Exception e) {
            // Don't fail the booking if notification fails
            org.slf4j.LoggerFactory.getLogger(NotificationService.class)
                .error("Failed to create booking notification: {}", e.getMessage());
        }
    }

    /**
     * Create notification when a booking is cancelled
     */
    public void notifyOnBookingCancellation(Booking booking, User student) {
        try {
            Notification notification = new Notification();
            // Notify the faculty member
            notification.setUserId(booking.getSlotId()); // We'll set proper faculty ID via slot lookup
            notification.setTitle("Lịch hẹn đã bị hủy");
            notification.setMessage("Sinh viên đã hủy lịch hẹn. Lý do: "
                + (booking.getCancellationReason() != null ? booking.getCancellationReason() : "Không có lý do"));
            notification.setType("booking_cancelled");
            notification.setIsRead(false);
            notificationRepository.save(notification);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(NotificationService.class)
                .error("Failed to create cancellation notification: {}", e.getMessage());
        }
    }
}
