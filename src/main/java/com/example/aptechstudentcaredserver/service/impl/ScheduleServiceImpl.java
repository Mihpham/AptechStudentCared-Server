package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.entity.UserSubject; // Thêm import
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
import com.example.aptechstudentcaredserver.repository.UserSubjectRepository; // Thêm import
import com.example.aptechstudentcaredserver.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ClassRepository classRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectRepository userSubjectRepository; // Thêm repository

    @Override
    public ScheduleResponse getScheduleById(int scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
        return convertToResponse(schedule);
    }

    @Override
    public List<ScheduleResponse> getSchedulesByClassAndSubjectId(int classId, int subjectId) {
        List<Schedule> schedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        return convertToResponse(schedules);
    }

    @Override
    public List<ScheduleResponse> createSchedule(ScheduleRequest request, int classId, int subjectId) {
        UserSubject userSubject = userSubjectRepository.findByClassroomIdAndSubjectId(classId, subjectId)
                .orElseThrow(() -> new NotFoundException("UserSubject not found"));

        Class classEntity = userSubject.getClassroom();
        Subject subject = userSubject.getSubject();
        int numberOfSessions = userSubject.getNumberOfSessions();

        List<Schedule> schedules = createAndSaveSchedules(request.getStartDate(), classEntity, subject, numberOfSessions);
        return convertToResponse(schedules);
    }


    @Override
    public List<ScheduleResponse> updateSchedule(ScheduleRequest request, int classId, int subjectId) {
        // Tương tự như phương thức createSchedule, bạn có thể thêm logic cho update nếu cần
        return null;
    }

    private List<Schedule> createAndSaveSchedules(LocalDateTime startDate, Class classEntity, Subject subject, int numberOfSessions) {
        List<DayOfWeeks> classDays = classEntity.getDays();
        List<Schedule> schedules = new ArrayList<>();
        int sessionsCreated = 0;

        LocalDateTime currentDate = startDate;

        while (sessionsCreated < numberOfSessions) {
            for (DayOfWeeks day : classDays) {
                if (currentDate.getDayOfWeek().getValue() == day.getValue()) {
                    Schedule schedule = new Schedule();
                    schedule.setClasses(classEntity);
                    schedule.setSubject(subject);
                    schedule.setStartDate(currentDate);
                    schedule.setEndDate(currentDate); // Có thể sửa lại nếu có thời gian cụ thể

                    schedules.add(schedule);
                    sessionsCreated++;

                    if (sessionsCreated >= numberOfSessions) {
                        break;
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return scheduleRepository.saveAll(schedules);
    }

    private ScheduleResponse convertToResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setStartDate(schedule.getStartDate());
        response.setEndDate(schedule.getEndDate());
        response.setSubjectName(schedule.getSubject().getSubjectName());
        response.setClassName(schedule.getClasses().getClassName());
        return response;
    }

    private List<ScheduleResponse> convertToResponse(List<Schedule> schedules) {
        List<ScheduleResponse> responses = new ArrayList<>();
        for (Schedule schedule : schedules) {
            responses.add(convertToResponse(schedule));
        }
        return responses;
    }
}
