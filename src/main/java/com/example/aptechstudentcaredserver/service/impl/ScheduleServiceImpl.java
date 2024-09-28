package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.entity.UserSubject;
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
import com.example.aptechstudentcaredserver.repository.UserSubjectRepository;
import com.example.aptechstudentcaredserver.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserSubjectRepository userSubjectRepository;

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

        // Check if any schedules already exist
        List<Schedule> existingSchedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        if (!existingSchedules.isEmpty()) {
            throw new DuplicateException("Schedule already exists for this class and subject.");
        }

        int numberOfSessions = userSubject.getNumberOfSessions();
        List<Schedule> schedules = createAndSaveSchedules(request.getStartDate(), classEntity, subject, numberOfSessions);
        return convertToResponse(schedules);
    }

    @Override
    public List<ScheduleResponse> updateSchedule(ScheduleRequest request, int classId, int subjectId) {
        // Retrieve existing schedules
        List<Schedule> existingSchedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        if (existingSchedules.isEmpty()) {
            throw new NotFoundException("No existing schedules found for this class and subject.");
        }

        List<Schedule> updatedSchedules = new ArrayList<>();
        LocalDateTime newStartDate = request.getStartDate();
        LocalDateTime newEndDate = request.getEndDate();

        // Update existing schedules and extend if needed
        for (Schedule existingSchedule : existingSchedules) {
            if (existingSchedule.getEndDate().isBefore(newStartDate)) {
                // If the existing schedule ends before the new start date, we can extend it
                existingSchedule.setEndDate(newEndDate);
                updatedSchedules.add(existingSchedule);
            } else if (existingSchedule.getStartDate().isAfter(newEndDate)) {
                // If the existing schedule starts after the new end date, keep it as is
                updatedSchedules.add(existingSchedule);
            }
        }

        // Create new schedules if there's a gap
        if (newStartDate.isAfter(existingSchedules.get(existingSchedules.size() - 1).getEndDate())) {
            Class classEntity = existingSchedules.get(0).getClasses();
            Subject subject = existingSchedules.get(0).getSubject();
            List<Schedule> newSchedules = createAndSaveSchedules(newStartDate, classEntity, subject, calculateNumberOfSessions(newStartDate, newEndDate));
            updatedSchedules.addAll(newSchedules);
        }

        scheduleRepository.saveAll(updatedSchedules); // Save all updated schedules
        return convertToResponse(updatedSchedules);
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
                    schedule.setEndDate(currentDate); // Assuming end date is the same for a single session

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

    private int calculateNumberOfSessions(LocalDateTime startDate, LocalDateTime endDate) {
        return (int) java.time.Duration.between(startDate.toLocalDate().atStartOfDay(), endDate.toLocalDate().atStartOfDay()).toDays() + 1;
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
