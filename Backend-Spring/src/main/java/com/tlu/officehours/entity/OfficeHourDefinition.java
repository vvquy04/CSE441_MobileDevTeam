package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "office_hour_definitions")
@Getter
@Setter
@NoArgsConstructor
public class OfficeHourDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DefinitionId")
    private Long definitionId;

    @Column(name = "faculty_user_id", nullable = false)
    private Long facultyUserId;

    @Column(name = "DayOfWeek", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "SlotDurationMinutes", nullable = false)
    private Integer slotDurationMinutes;

    @Column(name = "MaxStudentsPerSlot", nullable = false)
    private Integer maxStudentsPerSlot;

    @Column(name = "Note", length = 500)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
