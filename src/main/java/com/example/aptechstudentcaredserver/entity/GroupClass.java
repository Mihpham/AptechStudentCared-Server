package com.example.aptechstudentcaredserver.entity;

import com.example.aptechstudentcaredserver.enums.ClassMemberStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_classes")
@Data
public class GroupClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classes;

    @Column(name = "joined_date", nullable = false)
    private LocalDateTime joinedDate;

    @Column(name = "outed_date")
    private LocalDateTime outedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassMemberStatus status;
}
