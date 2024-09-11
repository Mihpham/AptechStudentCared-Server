package com.example.aptechstudentcaredserver.entity;

import com.example.aptechstudentcaredserver.enums.DayOfWeeks;
import com.example.aptechstudentcaredserver.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "classes")
@Data
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "class_name", length = 255, nullable = false)
    private String className;

    private String center;

    private String hour;

    private String days;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "classes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<GroupClass> groupClasses;

    @OneToMany(mappedBy = "classes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

