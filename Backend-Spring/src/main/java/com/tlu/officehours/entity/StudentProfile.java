package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
public class StudentProfile {

    @Id
    @Column(name = "StudentUserId")
    private Long studentUserId;

    @Column(name = "StudentName", nullable = false)
    private String studentName;

    @Column(name = "StudentCode", length = 50, unique = true)
    private String studentCode;

    @Column(name = "ClassName", length = 50)
    private String className;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "avatar")
    private String avatar;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StudentUserId", referencedColumnName = "UserId", insertable = false, updatable = false)
    private User user;
}
