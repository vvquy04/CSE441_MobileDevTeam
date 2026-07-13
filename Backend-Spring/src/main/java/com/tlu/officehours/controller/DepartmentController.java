package com.tlu.officehours.controller;

import com.tlu.officehours.entity.Department;
import com.tlu.officehours.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * GET /api/departments
     * Returns departments matching Laravel's format: [{DepartmentId, Name}]
     */
    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        List<Department> departments = departmentRepository.findAll();

        // Match Laravel response format: "DepartmentName as Name"
        List<Map<String, Object>> response = departments.stream().map(dept -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("DepartmentId", dept.getDepartmentId());
            map.put("Name", dept.getDepartmentName());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
