package com.tlu.officehours.service;

import com.tlu.officehours.entity.AvailableSlot;
import com.tlu.officehours.repository.AvailableSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlotService {

    @Autowired
    private AvailableSlotRepository availableSlotRepository;

    /**
     * Get available slots for a faculty member on a specific date
     */
    public List<Map<String, Object>> getAvailableSlots(Long facultyUserId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<AvailableSlot> slots = availableSlotRepository
            .findAvailableSlotsByFacultyAndDate(facultyUserId, startOfDay, endOfDay);

        return slots.stream().map(slot -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("SlotId", slot.getSlotId());
            map.put("faculty_user_id", slot.getFacultyUserId());
            map.put("StartTime", slot.getStartTime());
            map.put("EndTime", slot.getEndTime());
            map.put("MaxStudents", slot.getMaxStudents());
            map.put("IsAvailable", slot.getIsAvailable());
            map.put("DefinitionId", slot.getDefinitionId());
            return map;
        }).collect(Collectors.toList());
    }
}
