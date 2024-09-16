package com.example.aptechstudentcaredserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "semesters")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "semester")
    private Set<CourseSubject> courseSubjects;

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Class> classes;
}
