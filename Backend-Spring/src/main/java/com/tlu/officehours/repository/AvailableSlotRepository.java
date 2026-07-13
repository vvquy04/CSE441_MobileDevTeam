package com.tlu.officehours.repository;

import com.tlu.officehours.entity.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {

    @Query("SELECT s FROM AvailableSlot s WHERE s.facultyUserId = :facultyUserId " +
           "AND s.startTime >= :startOfDay AND s.startTime < :endOfDay " +
           "AND s.isAvailable = true ORDER BY s.startTime ASC")
    List<AvailableSlot> findAvailableSlotsByFacultyAndDate(
        @Param("facultyUserId") Long facultyUserId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    List<AvailableSlot> findByFacultyUserId(Long facultyUserId);

    @Query("SELECT s FROM AvailableSlot s LEFT JOIN FETCH s.bookings " +
           "WHERE s.facultyUserId = :fid ORDER BY s.startTime DESC")
    List<AvailableSlot> findByFacultyUserIdWithBookings(@Param("fid") Long fid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AvailableSlot s WHERE s.slotId = :slotId")
    Optional<AvailableSlot> findByIdForUpdate(@Param("slotId") Long slotId);

    long countByFacultyUserId(Long facultyUserId);

    long countByFacultyUserIdAndIsAvailable(Long facultyUserId, Boolean isAvailable);
}
