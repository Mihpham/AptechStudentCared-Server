package com.example.aptechstudentcaredserver.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            optional = false
    )
    private UserDetail userDetail;
    @OneToMany(mappedBy = "user")
    private List<UserSubject> userSubjects;

    @OneToMany(mappedBy = "user")
    private List<StudentPerformance> studentPerformances;

    @OneToMany(mappedBy = "user")
    private List<ClassMember> classMembers;

    @OneToMany(mappedBy = "user")
    private List<Classes> classes;

    @OneToMany(mappedBy = "user")
    private List<ExamDetail> examDetails;

    @OneToMany(mappedBy = "user")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "evaluator")
    private List<Evaluation> evaluationsAsEvaluator;

    @OneToMany(mappedBy = "evaluatee")
    private List<Evaluation> evaluationsAsEvaluatee;

    @OneToMany(mappedBy = "user")
    private List<HomeworkScore> homeworkScores;

    @OneToMany(mappedBy = "user")
    private List<UserCourse> userCourses;

    @OneToMany(mappedBy = "user")
    private List<Course> courses;

    @OneToMany(mappedBy = "user")
    private List<Schedule> schedules;
}
