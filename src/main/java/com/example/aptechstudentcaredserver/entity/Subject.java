package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "subject_name", length = 255, nullable = false)
    private String subjectName;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "subject")
    private List<UserSubject> userSubjects;

    @OneToMany(mappedBy = "subject")
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "subject")
    private List<ExamDetail> examDetails;

    @OneToMany(mappedBy = "subject")
    private List<StudentPerformance> studentPerformances;

    @OneToMany(mappedBy = "subject")
    private List<HomeworkScore> homeworkScores;
    @OneToMany(mappedBy = "subject")
    private List<CourseSubject> courseSubjects;
    @OneToMany(mappedBy = "subject")
    private List<Evaluation> evaluations;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


}