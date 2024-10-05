package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.entity.UserSubject;
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.enums.Status;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
import com.example.aptechstudentcaredserver.repository.UserSubjectRepository;
import com.example.aptechstudentcaredserver.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDate;
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
        List<Schedule> schedules = createAndSaveSchedules(request.getStartDate(), request.getStatus(), request.getNote(), classEntity, subject, numberOfSessions);
        return convertToResponse(schedules);
    }

    @Override
    public List<ScheduleResponse> updateSchedule(ScheduleRequest request, int classId, int subjectId) {
        // Retrieve the user subject to get the number of sessions and class days
        UserSubject userSubject = userSubjectRepository.findByClassroomIdAndSubjectId(classId, subjectId)
                .orElseThrow(() -> new NotFoundException("UserSubject not found"));

        List<Schedule> existingSchedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        List<DayOfWeeks> classDays = userSubject.getClassroom().getDays();

        LocalDate currentDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        List<Schedule> newSchedules = new ArrayList<>();

        // Iterate over the date range and add new schedules for the specified days
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            for (DayOfWeeks day : classDays) {
                if (currentDate.getDayOfWeek().getValue() == day.getValue()) {
                    // Capture the current date to avoid lambda final variable issue
                    LocalDate finalCurrentDate = currentDate;

                    // Check if the schedule already exists
                    boolean exists = existingSchedules.stream()
                            .anyMatch(schedule -> schedule.getStartDate().isEqual(finalCurrentDate));

                    if (!exists) {
                        // Create a new schedule
                        Schedule newSchedule = new Schedule();
                        newSchedule.setClasses(existingSchedules.get(0).getClasses());
                        newSchedule.setSubject(existingSchedules.get(0).getSubject());
                        newSchedule.setStartDate(finalCurrentDate);
                        newSchedule.setEndDate(finalCurrentDate); // Assuming endDate is the same as startDate
                        newSchedule.setStatus(Status.SCHEDULED);
                        newSchedule.setNote(request.getNote());

                        newSchedules.add(newSchedule);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        // Save the new schedules if any
        if (!newSchedules.isEmpty()) {
            scheduleRepository.saveAll(newSchedules);
        }

        // Combine existing schedules with new ones and return the response
        existingSchedules.addAll(newSchedules);
        return convertToResponse(existingSchedules);
    }

    @Override
    public ScheduleResponse updateScheduleById(int scheduleId, ScheduleRequest request) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        if (!existingSchedule.getStartDate().equals(request.getStartDate())) {
            List<Schedule> conflictingSchedules = scheduleRepository.findByStartDateAndClassesId(
                    request.getStartDate(), existingSchedule.getClasses().getId());
            if (!conflictingSchedules.isEmpty()) {
                throw new DuplicateException("A schedule already exists for this start date.");
            }
        }

        existingSchedule.setStartDate(request.getStartDate());
        existingSchedule.setEndDate(request.getStartDate());
        existingSchedule.setStatus(Status.valueOf(request.getStatus()));
        existingSchedule.setNote(request.getNote());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);

        return convertToResponse(updatedSchedule);
    }

    @Override
    public void deleteScheduleById(int scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found with id " + scheduleId));

        scheduleRepository.delete(schedule);
    }

    private List<Schedule> createAndSaveSchedules(LocalDate startDate, String status, String note, Class classEntity, Subject subject, int numberOfSessions) {
        List<DayOfWeeks> classDays = classEntity.getDays();
        List<Schedule> schedules = new ArrayList<>();
        int sessionsCreated = 0;
        LocalDate currentDate = startDate;

        while (sessionsCreated < numberOfSessions) {
            for (DayOfWeeks day : classDays) {
                if (currentDate.getDayOfWeek().getValue() == day.getValue()) {
                    Schedule schedule = new Schedule();
                    schedule.setClasses(classEntity);
                    schedule.setSubject(subject);
                    schedule.setStartDate(currentDate);
                    schedule.setEndDate(currentDate);
                    schedule.setStatus(Status.SCHEDULED);
                    schedule.setNote(note);

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

    private int calculateNumberOfSessions(LocalDate startDate, LocalDate endDate) {
        return (int) java.time.Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays() + 1;
    }

    private ScheduleResponse convertToResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setStartDate(schedule.getStartDate());
        response.setEndDate(schedule.getEndDate());
        response.setSubjectCode(schedule.getSubject().getSubjectCode());
        response.setClassName(schedule.getClasses().getClassName());
        response.setStatus(schedule.getStatus().name());
        response.setNote(schedule.getNote());
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
