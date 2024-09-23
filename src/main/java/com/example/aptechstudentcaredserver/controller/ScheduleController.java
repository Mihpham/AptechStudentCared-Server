package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{scheduleId}") // Endpoint mới
    public ResponseEntity<Schedule> getScheduleById(@PathVariable int scheduleId) {
            Schedule schedule = scheduleService.getScheduleById(scheduleId);
            return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getSchedulesByClassId(@PathVariable int classId) {
        List<Schedule> schedules = scheduleService.getSchedulesByClassId(classId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PostMapping("/create/class/{classId}")
    public ResponseEntity<List<Schedule>> createSchedule(
            @PathVariable int classId,
            @RequestBody ScheduleRequest request) {

        List<Schedule> schedules = scheduleService.createSchedule(request, classId);
        return new ResponseEntity<>(schedules, HttpStatus.CREATED);
    }

    @PutMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> updateSchedule(
            @PathVariable int classId,
            @RequestBody ScheduleRequest request) {

        List<Schedule> schedules = scheduleService.updateSchedule(request, classId);
        return new ResponseEntity<>(schedules, HttpStatus.CREATED);
    }

}
