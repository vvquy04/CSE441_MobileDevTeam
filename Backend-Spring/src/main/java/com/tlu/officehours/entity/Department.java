package com.tlu.officehours.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
public class Department {

    @Id
    @Column(name = "DepartmentId")
    private String departmentId;

    @Column(name = "DepartmentName", unique = true, nullable = false)
    private String departmentName;
}
