package com.example.aptechstudentcaredserver.enums;


import lombok.Getter;

@Getter
public enum DayOfWeeks {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

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

