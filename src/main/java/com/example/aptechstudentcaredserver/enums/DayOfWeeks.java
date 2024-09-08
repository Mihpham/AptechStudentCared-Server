package com.example.aptechstudentcaredserver.enums;

import lombok.Getter;

@Getter
public enum DayOfWeeks {
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

    private final int value;

    DayOfWeeks(int value) {
        this.value = value;
    }

    // Case-insensitive matching for day names
    public static DayOfWeeks fromString(String day) {
        try {
            return DayOfWeeks.valueOf(day.toUpperCase()); // Convert to uppercase to match enum constants
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid day format: " + day);
        }
    }

    public static DayOfWeeks fromValue(int value) {
        for (DayOfWeeks day : values()) {
            if (day.getValue() == value) {
                return day;
            }
        }
        throw new RuntimeException("Invalid day value: " + value);
    }
}

