package com.tlu.officehours.controller;

import com.tlu.officehours.service.SlotService;
import com.tlu.officehours.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SlotService slotService;

    /**
     * GET /api/student/featured-teachers
     */
    @GetMapping("/student/featured-teachers")
    public ResponseEntity<?> getFeaturedTeachers() {
        List<Map<String, Object>> teachers = teacherService.getFeaturedTeachers();
        return ResponseEntity.ok(teachers);
    }

    /**
     * GET /api/student/teachers-by-department/{departmentId}
     */
    @GetMapping("/student/teachers-by-department/{departmentId}")
    public ResponseEntity<?> getTeachersByDepartment(@PathVariable("departmentId") String departmentId) {
        List<Map<String, Object>> teachers = teacherService.getTeachersByDepartment(departmentId);
        return ResponseEntity.ok(teachers);
    }

    /**
     * GET /api/student/search-teachers?query=xxx
     */
    @GetMapping("/student/search-teachers")
    public ResponseEntity<?> searchTeachers(@RequestParam(value = "query", required = false) String query) {
        List<Map<String, Object>> teachers = teacherService.searchTeachers(query);
        return ResponseEntity.ok(teachers);
    }

    /**
     * GET /api/student/teacher/{facultyUserId}
     */
    @GetMapping("/student/teacher/{facultyUserId}")
    public ResponseEntity<?> getTeacherDetail(@PathVariable("facultyUserId") Long facultyUserId) {
        Map<String, Object> teacher = teacherService.getTeacherDetail(facultyUserId);
        return ResponseEntity.ok(teacher);
    }

    /**
     * GET /api/faculty/{facultyUserId}/available-slots?date=2025-07-01
     */
    @GetMapping("/faculty/{facultyUserId}/available-slots")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable("facultyUserId") Long facultyUserId,
            @RequestParam("date") String date
    ) {
        List<Map<String, Object>> slots = slotService.getAvailableSlots(facultyUserId, date);
        return ResponseEntity.ok(slots);
    }
}
