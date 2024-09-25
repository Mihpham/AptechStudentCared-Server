package com.example.aptechstudentcaredserver.service.impl;

import com.example.aptechstudentcaredserver.bean.request.ScheduleRequest;
import com.example.aptechstudentcaredserver.bean.response.ScheduleResponse;
import com.example.aptechstudentcaredserver.entity.Class;
import com.example.aptechstudentcaredserver.entity.Schedule;
import com.example.aptechstudentcaredserver.entity.Subject;
import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.exception.DuplicateException;
import com.example.aptechstudentcaredserver.exception.NotFoundException;
import com.example.aptechstudentcaredserver.repository.ClassRepository;
import com.example.aptechstudentcaredserver.repository.ScheduleRepository;
import com.example.aptechstudentcaredserver.repository.SubjectRepository;
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
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        List<Schedule> existingSchedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        if (!existingSchedules.isEmpty()) {
            throw new DuplicateException("Schedule already exists for this class and subject. Please update instead.");
        }

        List<Schedule> schedules = createAndSaveSchedules(request, classEntity, subject);
        return convertToResponse(schedules);
    }

    @Override
    public List<ScheduleResponse> updateSchedule(ScheduleRequest request, int classId, int subjectId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        List<Schedule> existingSchedules = scheduleRepository.findByClassesIdAndSubjectId(classId, subjectId);
        if (!existingSchedules.isEmpty()) {
            scheduleRepository.deleteAll(existingSchedules);
        }

        List<Schedule> schedules = createAndSaveSchedules(request, classEntity, subject);
        return convertToResponse(schedules);
    }

    private List<Schedule> createAndSaveSchedules(ScheduleRequest request, Class classEntity, Subject subject) {
        List<Schedule> schedules = createNewSchedules(request, classEntity, subject);
        return scheduleRepository.saveAll(schedules);
    }

    private List<Schedule> createNewSchedules(ScheduleRequest request, Class classEntity, Subject subject) {
        List<DayOfWeeks> classDays = classEntity.getDays();
        List<Schedule> schedules = new ArrayList<>();

        LocalDateTime currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            for (DayOfWeeks day : classDays) {
                if (currentDate.getDayOfWeek().getValue() == day.getValue()) {
                    Schedule schedule = new Schedule();
                    schedule.setClasses(classEntity);
                    schedule.setSubject(subject);
                    schedule.setStartDate(currentDate);
                    schedule.setEndDate(currentDate);

                    schedules.add(schedule);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return schedules;
    }

    private ScheduleResponse convertToResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setStartDate(schedule.getStartDate());
        response.setEndDate(schedule.getEndDate());
        response.setSubjectName(schedule.getSubject().getSubjectName()); // Đảm bảo phương thức getSubjectName() đã được định nghĩa
        response.setClassName(schedule.getClasses().getClassName()); // Đảm bảo phương thức getClassName() đã được định nghĩa
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

