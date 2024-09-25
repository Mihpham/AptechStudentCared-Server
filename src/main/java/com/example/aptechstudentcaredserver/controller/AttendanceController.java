package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.AttendanceRequest;
import com.example.aptechstudentcaredserver.bean.response.AttendanceResponse;
import com.example.aptechstudentcaredserver.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendances() {
        List<AttendanceResponse> attendances = attendanceService.findAllAttendances();
        return ResponseEntity.ok(attendances);
    }

    @PutMapping("/update/user/{userId}/schedule/{scheduleId}")
    public ResponseEntity<AttendanceResponse> updateOrCreateAttendance(
            @PathVariable int userId,
            @PathVariable int scheduleId,
            @RequestBody AttendanceRequest request) {

        AttendanceResponse response = attendanceService.updateOrCreateAttendance(userId, scheduleId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
