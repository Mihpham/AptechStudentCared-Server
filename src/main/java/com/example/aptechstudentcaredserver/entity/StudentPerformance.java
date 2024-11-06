package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_performances")
public class StudentPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private int presentCount;
    private int presentWithPermissionCount;
    private int absentCount;

    @Column(name = "theory_exam_score", precision = 5, scale = 2)
    private BigDecimal theoryExamScore;

    @Column(name = "practical_exam_score", precision = 5, scale = 2)
    private BigDecimal practicalExamScore;

//    @Column(name = "evaluation_score", precision = 5, scale = 2)
//    private BigDecimal evaluationScore;

    @Column(name = "attendance_percentage", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;

    @Column(name = "practical_percentage")
    private BigDecimal practicalPercentage;

    @Column(name = "theoretical_percentage")
    private BigDecimal theoreticalPercentage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String firstSubjectSchedules;
    private String lastSubjectSchedules;
}
