package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course_subject")
public class CourseSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

}
