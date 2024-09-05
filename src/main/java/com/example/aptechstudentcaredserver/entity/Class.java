package com.example.aptechstudentcaredserver.entity;

import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "classes")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "class_name", length = 255, nullable = false)
    private String className;

    private String center;

    private String hour;

    @ElementCollection(targetClass = DayOfWeeks.class)
    @Enumerated(EnumType.STRING)
    private List<DayOfWeeks> days;

    @Column(name = "Admission_Date")
    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "classes")
    private Set<GroupClass> groupClasses;

    @OneToMany(mappedBy = "classes")
    private List<Schedule> schedules;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}