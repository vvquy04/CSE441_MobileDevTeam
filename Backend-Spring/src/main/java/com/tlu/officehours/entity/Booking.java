package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingId")
    private Long bookingId;

    @Column(name = "SlotId", nullable = false)
    private Long slotId;

    @Column(name = "StudentUserId", nullable = false)
    private Long studentUserId;

    @Column(name = "BookingTime", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "Purpose", length = 500)
    private String purpose;

    @Column(name = "Status", length = 50, nullable = false)
    @Convert(converter = BookingStatusConverter.class)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "CancellationTime")
    private LocalDateTime cancellationTime;

    @Column(name = "CancellationReason", length = 500)
    private String cancellationReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SlotId", referencedColumnName = "SlotId", insertable = false, updatable = false)
    private AvailableSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentUserId", referencedColumnName = "UserId", insertable = false, updatable = false)
    private User student;
}
