package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "subject_name", length = 255, nullable = false)
    private String subjectName;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "total_hours")
    private int totalHours;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSubject> userSubjects;

    @OneToMany(mappedBy = "subject")
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "subject")
    private List<ExamDetail> examDetails;

    @OneToMany(mappedBy = "subject")
    private List<StudentPerformance> studentPerformances;

    @OneToMany(mappedBy = "subject")
    private List<HomeworkScore> homeworkScores;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseSubject> courseSubjects;

    @OneToMany(mappedBy = "subject")
    private List<Evaluation> evaluations;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}