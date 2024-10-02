package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;
import com.example.aptechstudentcaredserver.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable int scheduleId) {
        ScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}/subject/{subjectId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByClassAndSubjectId(
            @PathVariable int classId,
            @PathVariable int subjectId) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByClassAndSubjectId(classId, subjectId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PostMapping("/create/class/{classId}/subject/{subjectId}")
    public ResponseEntity<List<ScheduleResponse>> createSchedule(
            @PathVariable int classId,
            @PathVariable int subjectId,
            @RequestBody ScheduleRequest request) {

        List<ScheduleResponse> responses = scheduleService.createSchedule(request, classId, subjectId);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @PutMapping("/class/{classId}/subject/{subjectId}")
    public ResponseEntity<List<ScheduleResponse>> updateSchedule(
            @PathVariable int classId,
            @PathVariable int subjectId,
            @RequestBody ScheduleRequest request) {

        List<ScheduleResponse> responses = scheduleService.updateSchedule(request, classId, subjectId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateScheduleById(
            @PathVariable int scheduleId,
            @RequestBody ScheduleRequest request) {

        ScheduleResponse updateSchedule = scheduleService.updateScheduleById(scheduleId, request);
        return new ResponseEntity<>(updateSchedule, HttpStatus.OK);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteScheduleById(@PathVariable int scheduleId) {
        scheduleService.deleteScheduleById(scheduleId);
        return ResponseEntity.ok(Map.of("message", "Subject deleted successfully"));
    }
}
