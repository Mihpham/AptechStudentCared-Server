package com.example.aptechstudentcaredserver.entity;

import com.example.aptechstudentcaredserver.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @ManyToOne
    @JoinColumn(name = "evaluatee_id") // Tên cột khóa ngoại
    private User evaluatee;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
}