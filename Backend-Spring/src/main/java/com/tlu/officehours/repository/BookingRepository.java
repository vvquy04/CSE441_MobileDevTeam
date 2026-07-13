package com.tlu.officehours.repository;

import com.tlu.officehours.entity.Booking;
import com.tlu.officehours.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot s JOIN FETCH s.faculty " +
           "WHERE b.studentUserId = :studentUserId ORDER BY b.bookingTime DESC")
    List<Booking> findByStudentUserIdWithSlotAndFaculty(@Param("studentUserId") Long studentUserId);

    List<Booking> findByStudentUserId(Long studentUserId);

    boolean existsBySlotIdAndStudentUserId(Long slotId, Long studentUserId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slotId = :slotId " +
           "AND b.status IN :statuses")
    long countActiveBookingsBySlotId(
        @Param("slotId") Long slotId,
        @Param("statuses") List<BookingStatus> statuses
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.studentUserId = :userId")
    long countByStudentUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.studentUserId = :userId " +
           "AND b.status IN :statuses")
    long countUpcomingByStudentUserId(
        @Param("userId") Long userId,
        @Param("statuses") List<BookingStatus> statuses
    );

    // ==================== FACULTY QUERIES ====================

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot s LEFT JOIN FETCH b.student u " +
           "LEFT JOIN FETCH u.studentProfile " +
           "WHERE s.facultyUserId = :facultyUserId ORDER BY b.bookingTime DESC")
    List<Booking> findByFacultyUserIdWithDetails(@Param("facultyUserId") Long facultyUserId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot s LEFT JOIN FETCH b.student u " +
           "LEFT JOIN FETCH u.studentProfile " +
           "WHERE s.facultyUserId = :facultyUserId AND b.status = :status " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findByFacultyAndStatusWithDetails(
        @Param("facultyUserId") Long facultyUserId,
        @Param("status") BookingStatus status
    );

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot s LEFT JOIN FETCH b.student u " +
           "LEFT JOIN FETCH u.studentProfile " +
           "WHERE s.facultyUserId = :facultyUserId " +
           "AND s.startTime >= :startOfDay AND s.startTime < :endOfDay " +
           "ORDER BY s.startTime ASC")
    List<Booking> findByFacultyAndDateWithDetails(
        @Param("facultyUserId") Long facultyUserId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT b FROM Booking b JOIN FETCH b.slot s LEFT JOIN FETCH b.student u " +
           "LEFT JOIN FETCH u.studentProfile " +
           "WHERE s.facultyUserId = :facultyUserId " +
           "AND s.startTime >= :startDate AND s.startTime < :endDate " +
           "ORDER BY s.startTime ASC")
    List<Booking> findByFacultyAndDateRangeWithDetails(
        @Param("facultyUserId") Long facultyUserId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(b) FROM Booking b JOIN b.slot s WHERE s.facultyUserId = :fid")
    long countByFacultyUserId(@Param("fid") Long fid);

    @Query("SELECT COUNT(b) FROM Booking b JOIN b.slot s WHERE s.facultyUserId = :fid AND b.status = :status")
    long countByFacultyAndStatus(@Param("fid") Long fid, @Param("status") BookingStatus status);
}
