package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "student_performance")
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

    @Column(name = "theory_exam_score", precision = 5, scale = 2)
    private BigDecimal theoryExamScore;

    @Column(name = "practical_exam_score", precision = 5, scale = 2)
    private BigDecimal practicalExamScore;

    @Column(name = "evaluation_score", precision = 5, scale = 2)
    private BigDecimal evaluationScore;

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;
}
