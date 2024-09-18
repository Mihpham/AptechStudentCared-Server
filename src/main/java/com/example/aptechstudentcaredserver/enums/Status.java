package com.example.aptechstudentcaredserver.enums;

public enum Status {
    /**
     * Common status for all entity
     */
    ACTIVE, DELETE,

    /**
     * Status for class
     */
    STUDYING,FINISHED,CANCEL,SCHEDULED,

    /**
     * Status for student
     */
    DELAY,DROPOUT,GRADUATED,

    /**
     * Status for teacher
     */
    TEACHING,ONLEAVE,LEAVE,

}
