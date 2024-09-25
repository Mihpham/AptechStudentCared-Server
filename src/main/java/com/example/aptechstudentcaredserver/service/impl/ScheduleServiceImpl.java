package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
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

    @Override
    public Schedule getScheduleById(int scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
    }

    @Override
    public List<Schedule> getSchedulesByClassId(int classId) {
        return scheduleRepository.findByClassesId(classId);
    }

    @Override
    public List<Schedule> createSchedule(ScheduleRequest request, int classId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found"));

        List<Schedule> existingSchedules = scheduleRepository.findByClassesId(classId);
        if (!existingSchedules.isEmpty()) {
            throw new DuplicateException("Schedule already exists for this class. Please update instead.");
        }

        return createAndSaveSchedules(request, classEntity);
    }

    @Override
    public List<Schedule> updateSchedule(ScheduleRequest request, int classId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found"));

        List<Schedule> existingSchedules = scheduleRepository.findByClassesId(classId);
        if (!existingSchedules.isEmpty()) {
            scheduleRepository.deleteAll(existingSchedules);
        }

        return createAndSaveSchedules(request, classEntity);
    }

    private List<Schedule> createAndSaveSchedules(ScheduleRequest request, Class classEntity) {
        List<Schedule> schedules = createNewSchedules(request, classEntity);
        return scheduleRepository.saveAll(schedules);
    }

    private List<Schedule> createNewSchedules(ScheduleRequest request, Class classEntity) {
        List<DayOfWeeks> classDays = classEntity.getDays();
        List<Schedule> schedules = new ArrayList<>();

        LocalDateTime currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            for (DayOfWeeks day : classDays) {
                if (currentDate.getDayOfWeek().getValue() == day.getValue()) {
                    Schedule schedule = new Schedule();
                    schedule.setClasses(classEntity);

                    schedule.setStartDate(currentDate);
                    schedule.setEndDate(currentDate);

                    schedules.add(schedule);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return schedules;
    }

}
