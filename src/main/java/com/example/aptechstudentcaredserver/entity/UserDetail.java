package com.example.aptechstudentcaredserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    private String address;
    private String gender;
    private String image;

    @Column(name = "date_of_birth")
    private String dob;

    @Column(name = "roll_number")
    private String rollNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "parent_id", nullable = true)  // nullable = true allows the parent_id to be null
    private Parent parent;
}