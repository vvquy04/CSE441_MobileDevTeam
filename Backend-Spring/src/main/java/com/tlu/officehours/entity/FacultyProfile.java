package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "faculty_profiles")
@Getter
@Setter
@NoArgsConstructor
public class FacultyProfile {

    @Id
    @Column(name = "faculty_user_id")
    private Long facultyUserId;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "degree")
    private String degree;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "office_location")
    private String officeLocation;

    @Column(name = "avatar")
    private String avatar;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_user_id", referencedColumnName = "UserId", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "DepartmentId", insertable = false, updatable = false)
    private Department department;
}
