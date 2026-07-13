package com.tlu.officehours.service;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.exception.BadRequestException;
import com.tlu.officehours.exception.ResourceNotFoundException;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FacultySlotService {

    @Autowired
    private AvailableSlotRepository slotRepository;

    @Autowired
    private OfficeHourDefinitionRepository definitionRepository;

    @Autowired
    private FacultyService facultyService;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * GET /api/faculty/slots
     */
    public List<Map<String, Object>> getAllSlots(User user) {
        return slotRepository.findByFacultyUserIdWithBookings(user.getUserId())
            .stream().map(facultyService::toSlotResponse).toList();
    }

    /**
     * POST /api/faculty/slots - create single slot
     */
    @Transactional
    public Map<String, Object> createSlot(User user, Map<String, Object> request) {
        String startTimeStr = (String) request.get("start_time");
        String endTimeStr = (String) request.get("end_time");
        int maxStudents = request.get("max_students") != null
            ? ((Number) request.get("max_students")).intValue() : 1;
        Long definitionId = request.get("definition_id") != null
            ? ((Number) request.get("definition_id")).longValue() : null;

        AvailableSlot slot = new AvailableSlot();
        slot.setFacultyUserId(user.getUserId());
        slot.setStartTime(LocalDateTime.parse(startTimeStr, DT_FORMAT));
        slot.setEndTime(LocalDateTime.parse(endTimeStr, DT_FORMAT));
        slot.setMaxStudents(maxStudents);
        slot.setIsAvailable(true);
        slot.setDefinitionId(definitionId);
        slot = slotRepository.save(slot);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Tạo lịch trống thành công!");
        response.put("slot", facultyService.toSlotResponse(slot));
        return response;
    }

    /**
     * POST /api/faculty/slots/multiple - create multiple slots with a definition
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> createMultipleSlots(User user, Map<String, Object> request) {
        List<Map<String, String>> slotsData = (List<Map<String, String>>) request.get("slots");
        int maxStudents = request.get("max_students") != null
            ? ((Number) request.get("max_students")).intValue() : 1;
        int slotDuration = request.get("slot_duration") != null
            ? ((Number) request.get("slot_duration")).intValue() : 30;
        String notes = (String) request.get("notes");

        if (slotsData == null || slotsData.isEmpty()) {
            throw new BadRequestException("Danh sách slot không được trống.");
        }

        // Create definition
        Map<String, String> firstSlot = slotsData.get(0);
        Map<String, String> lastSlot = slotsData.get(slotsData.size() - 1);
        LocalDateTime firstStart = LocalDateTime.parse(firstSlot.get("start_time"), DT_FORMAT);
        LocalDateTime lastEnd = LocalDateTime.parse(lastSlot.get("end_time"), DT_FORMAT);

        OfficeHourDefinition definition = new OfficeHourDefinition();
        definition.setFacultyUserId(user.getUserId());
        definition.setDayOfWeek(firstStart.getDayOfWeek().getValue());
        definition.setStartTime(firstStart.toLocalTime());
        definition.setEndTime(lastEnd.toLocalTime());
        definition.setSlotDurationMinutes(slotDuration);
        definition.setMaxStudentsPerSlot(maxStudents);
        definition.setNote(notes);
        definition = definitionRepository.save(definition);

        // Create slots
        List<Map<String, Object>> createdSlots = new ArrayList<>();
        for (Map<String, String> slotData : slotsData) {
            AvailableSlot slot = new AvailableSlot();
            slot.setFacultyUserId(user.getUserId());
            slot.setStartTime(LocalDateTime.parse(slotData.get("start_time"), DT_FORMAT));
            slot.setEndTime(LocalDateTime.parse(slotData.get("end_time"), DT_FORMAT));
            slot.setMaxStudents(maxStudents);
            slot.setIsAvailable(true);
            slot.setDefinitionId(definition.getDefinitionId());
            slot = slotRepository.save(slot);
            createdSlots.add(facultyService.toSlotResponse(slot));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đã tạo thành công " + createdSlots.size() + " lịch trống!");
        response.put("definition", definition);
        response.put("slots", createdSlots);
        return response;
    }

    /**
     * POST /api/faculty/slots/monthly - create monthly schedule
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> createMonthlySchedule(User user, Map<String, Object> request) {
        List<Map<String, String>> slotsData = (List<Map<String, String>>) request.get("slots");
        int maxStudents = request.get("max_students") != null
            ? ((Number) request.get("max_students")).intValue() : 1;
        int slotDuration = request.get("slot_duration") != null
            ? ((Number) request.get("slot_duration")).intValue() : 30;
        String notes = (String) request.get("notes");
        int month = request.get("month") != null
            ? ((Number) request.get("month")).intValue() : LocalDate.now().getMonthValue();
        int year = request.get("year") != null
            ? ((Number) request.get("year")).intValue() : LocalDate.now().getYear();

        if (slotsData == null || slotsData.isEmpty()) {
            throw new BadRequestException("Danh sách slot không được trống.");
        }

        // Determine day of week from first slot
        Map<String, String> firstSlot = slotsData.get(0);
        LocalDateTime firstStart = LocalDateTime.parse(firstSlot.get("start_time"), DT_FORMAT);
        DayOfWeek dayOfWeek = firstStart.getDayOfWeek();

        // Create definition
        Map<String, String> lastSlot = slotsData.get(slotsData.size() - 1);
        LocalDateTime lastEnd = LocalDateTime.parse(lastSlot.get("end_time"), DT_FORMAT);

        OfficeHourDefinition definition = new OfficeHourDefinition();
        definition.setFacultyUserId(user.getUserId());
        definition.setDayOfWeek(dayOfWeek.getValue());
        definition.setStartTime(firstStart.toLocalTime());
        definition.setEndTime(lastEnd.toLocalTime());
        definition.setSlotDurationMinutes(slotDuration);
        definition.setMaxStudentsPerSlot(maxStudents);
        definition.setNote(notes);
        definition = definitionRepository.save(definition);

        // Find all matching days in the month
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Map<String, Object>> allCreatedSlots = new ArrayList<>();
        LocalDate current = startOfMonth;
        while (!current.isAfter(endOfMonth)) {
            if (current.getDayOfWeek() == dayOfWeek) {
                // Create slots for this day
                for (Map<String, String> slotData : slotsData) {
                    LocalDateTime origStart = LocalDateTime.parse(slotData.get("start_time"), DT_FORMAT);
                    LocalDateTime origEnd = LocalDateTime.parse(slotData.get("end_time"), DT_FORMAT);

                    AvailableSlot slot = new AvailableSlot();
                    slot.setFacultyUserId(user.getUserId());
                    slot.setStartTime(current.atTime(origStart.toLocalTime()));
                    slot.setEndTime(current.atTime(origEnd.toLocalTime()));
                    slot.setMaxStudents(maxStudents);
                    slot.setIsAvailable(true);
                    slot.setDefinitionId(definition.getDefinitionId());
                    slot = slotRepository.save(slot);
                    allCreatedSlots.add(facultyService.toSlotResponse(slot));
                }
            }
            current = current.plusDays(1);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đã tạo thành công " + allCreatedSlots.size() + " lịch trống cho tháng " + month + "/" + year + "!");
        response.put("definition", definition);
        response.put("slots", allCreatedSlots);
        return response;
    }

    /**
     * PUT /api/faculty/slots/{id}
     */
    @Transactional
    public Map<String, Object> updateSlot(User user, Long slotId, Map<String, Object> request) {
        AvailableSlot slot = findFacultySlot(user, slotId);

        if (request.containsKey("start_time")) {
            slot.setStartTime(LocalDateTime.parse((String) request.get("start_time"), DT_FORMAT));
        }
        if (request.containsKey("end_time")) {
            slot.setEndTime(LocalDateTime.parse((String) request.get("end_time"), DT_FORMAT));
        }
        if (request.containsKey("max_students")) {
            slot.setMaxStudents(((Number) request.get("max_students")).intValue());
        }
        if (request.containsKey("is_available")) {
            slot.setIsAvailable((Boolean) request.get("is_available"));
        }

        slot = slotRepository.save(slot);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Cập nhật slot thành công!");
        response.put("slot", facultyService.toSlotResponse(slot));
        return response;
    }

    /**
     * DELETE /api/faculty/slots/{id}
     */
    @Transactional
    public Map<String, Object> deleteSlot(User user, Long slotId) {
        AvailableSlot slot = findFacultySlot(user, slotId);
        slotRepository.delete(slot);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa slot thành công!");
        return response;
    }

    /**
     * POST /api/faculty/slots/{id}/toggle
     */
    @Transactional
    public Map<String, Object> toggleAvailability(User user, Long slotId) {
        AvailableSlot slot = findFacultySlot(user, slotId);
        slot.setIsAvailable(!slot.getIsAvailable());
        slot = slotRepository.save(slot);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", slot.getIsAvailable() ? "Slot đã được mở." : "Slot đã được đóng.");
        response.put("slot", facultyService.toSlotResponse(slot));
        return response;
    }

    // ==================== Helpers ====================

    private AvailableSlot findFacultySlot(User user, Long slotId) {
        AvailableSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (!slot.getFacultyUserId().equals(user.getUserId())) {
            throw new BadRequestException("Unauthorized: Slot này không thuộc về bạn.");
        }

        return slot;
    }
}
