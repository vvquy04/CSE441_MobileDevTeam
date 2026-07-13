package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AvailableSlotRepository slotRepository;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/faculty/bookings
     */
    public List<Map<String, Object>> getAllBookings(User user) {
        return bookingRepository.findByFacultyUserIdWithDetails(user.getUserId())
            .stream().map(facultyService::toBookingResponse).toList();
    }

    /**
     * GET /api/faculty/bookings/pending
     */
    public List<Map<String, Object>> getPendingBookings(User user) {
        return bookingRepository.findByFacultyAndStatusWithDetails(user.getUserId(), BookingStatus.PENDING)
            .stream().map(facultyService::toBookingResponse).toList();
    }

    /**
     * GET /api/faculty/bookings/confirmed
     */
    public List<Map<String, Object>> getConfirmedBookings(User user) {
        return bookingRepository.findByFacultyAndStatusWithDetails(user.getUserId(), BookingStatus.CONFIRMED)
            .stream().map(facultyService::toBookingResponse).toList();
    }

    /**
     * GET /api/faculty/bookings/by-date?date=2025-07-01&status=pending
     */
    public Map<String, Object> getBookingsByDate(User user, String dateStr, String status) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByFacultyAndDateWithDetails(
            user.getUserId(), startOfDay, endOfDay
        );

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            BookingStatus bs = BookingStatus.fromValue(status);
            bookings = bookings.stream().filter(b -> b.getStatus() == bs).toList();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("date", dateStr);
        response.put("total_bookings", bookings.size());
        response.put("bookings", bookings.stream().map(facultyService::toBookingResponse).toList());
        return response;
    }

    /**
     * GET /api/faculty/bookings/by-week?start_date=...&end_date=...&status=...
     */
    public Map<String, Object> getBookingsByWeek(User user, String startDateStr, String endDateStr, String status) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        LocalDateTime startDt = startDate.atStartOfDay();
        LocalDateTime endDt = endDate.plusDays(1).atStartOfDay();

        List<Booking> bookings = bookingRepository.findByFacultyAndDateRangeWithDetails(
            user.getUserId(), startDt, endDt
        );

        if (status != null && !status.isEmpty()) {
            BookingStatus bs = BookingStatus.fromValue(status);
            bookings = bookings.stream().filter(b -> b.getStatus() == bs).toList();
        }

        // Group by date
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<Map<String, Object>>> bookingsByDate = new LinkedHashMap<>();
        for (Booking b : bookings) {
            String dateKey = b.getSlot().getStartTime().format(dateFmt);
            bookingsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>())
                .add(facultyService.toBookingResponse(b));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("start_date", startDateStr);
        response.put("end_date", endDateStr);
        response.put("total_bookings", bookings.size());
        response.put("bookings_by_date", bookingsByDate);
        response.put("all_bookings", bookings.stream().map(facultyService::toBookingResponse).toList());
        return response;
    }

    /**
     * GET /api/faculty/bookings/by-status?status=pending&limit=10
     */
    public Map<String, Object> getBookingsByStatus(User user, String status, Integer limit) {
        BookingStatus bs = BookingStatus.fromValue(status);
        List<Booking> bookings = bookingRepository.findByFacultyAndStatusWithDetails(user.getUserId(), bs);

        if (limit != null && limit > 0) {
            bookings = bookings.stream().limit(limit).toList();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status);
        response.put("total_bookings", bookings.size());
        response.put("bookings", bookings.stream().map(facultyService::toBookingResponse).toList());
        return response;
    }

    /**
     * POST /api/faculty/bookings/{id}/approve
     */
    @Transactional
    public Map<String, Object> approveBooking(User user, Long bookingId) {
        Booking booking = findFacultyBooking(user, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể duyệt booking đang ở trạng thái pending.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Booking đã được phê duyệt thành công!");
        response.put("booking", facultyService.toBookingResponse(booking));
        return response;
    }

    /**
     * POST /api/faculty/bookings/{id}/reject
     */
    @Transactional
    public Map<String, Object> rejectBooking(User user, Long bookingId, String reason) {
        Booking booking = findFacultyBooking(user, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể từ chối booking đang ở trạng thái pending.");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setCancellationReason(reason);
        booking.setCancellationTime(LocalDateTime.now());
        bookingRepository.save(booking);

        // Restore slot availability
        restoreSlotAvailability(booking.getSlotId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Booking đã bị từ chối.");
        response.put("booking", facultyService.toBookingResponse(booking));
        return response;
    }

    /**
     * POST /api/faculty/bookings/{id}/cancel
     */
    @Transactional
    public Map<String, Object> cancelBooking(User user, Long bookingId, String reason) {
        Booking booking = findFacultyBooking(user, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Chỉ có thể hủy booking pending hoặc confirmed.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancellationTime(LocalDateTime.now());
        bookingRepository.save(booking);

        restoreSlotAvailability(booking.getSlotId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Booking đã được hủy thành công.");
        response.put("booking", facultyService.toBookingResponse(booking));
        return response;
    }

    /**
     * POST /api/faculty/bookings/{id}/complete
     */
    @Transactional
    public Map<String, Object> markCompleted(User user, Long bookingId) {
        Booking booking = findFacultyBooking(user, bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Chỉ có thể đánh dấu hoàn thành booking đã confirmed.");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Booking đã được đánh dấu hoàn thành.");
        response.put("booking", facultyService.toBookingResponse(booking));
        return response;
    }

    // ==================== Helpers ====================

    private Booking findFacultyBooking(User user, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify this booking belongs to the faculty's slots
        AvailableSlot slot = slotRepository.findById(booking.getSlotId())
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (!slot.getFacultyUserId().equals(user.getUserId())) {
            throw new BadRequestException("Unauthorized: Booking này không thuộc lịch của bạn.");
        }

        return booking;
    }

    private void restoreSlotAvailability(Long slotId) {
        AvailableSlot slot = slotRepository.findById(slotId).orElse(null);
        if (slot != null && !slot.getIsAvailable()) {
            slot.setIsAvailable(true);
            slotRepository.save(slot);
        }
    }
}
