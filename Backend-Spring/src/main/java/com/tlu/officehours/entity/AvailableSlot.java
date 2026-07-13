package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "available_slots")
@Getter
@Setter
@NoArgsConstructor
public class AvailableSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SlotId")
    private Long slotId;

    @Column(name = "faculty_user_id", nullable = false)
    private Long facultyUserId;

    @Column(name = "StartTime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "MaxStudents", nullable = false)
    private Integer maxStudents = 1;

    @Column(name = "IsAvailable", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "DefinitionId")
    private Long definitionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_user_id", referencedColumnName = "UserId", insertable = false, updatable = false)
    private User faculty;

    @OneToMany(mappedBy = "slot", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Count active bookings (pending or confirmed)
     */
    public long getActiveBookingsCount() {
        return bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.CONFIRMED)
            .count();
    }

    public boolean isFull() {
        return getActiveBookingsCount() >= maxStudents;
    }

    public long getAvailableSpots() {
        return maxStudents - getActiveBookingsCount();
    }
}
