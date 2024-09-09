package com.example.aptechstudentcaredserver.enums;

import lombok.Getter;

@Getter
public enum DayOfWeeks {
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7),
    SUNDAY(8);

    private final int value;

    DayOfWeeks(int value) {
        this.value = value;
    }

    public static DayOfWeeks fromValue(int value) {
        for (DayOfWeeks day : values()) {
            if (day.getValue() == value) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day value: " + value);
    }

    public int getDayValue() {
        return this.value;
    }
}
