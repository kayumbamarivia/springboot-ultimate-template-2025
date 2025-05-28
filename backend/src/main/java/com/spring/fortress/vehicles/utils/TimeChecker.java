package com.spring.fortress.vehicles.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeChecker {
    public static void main(String[] args) {
        long now = System.currentTimeMillis() / 1000;
        Instant instant = Instant.ofEpochSecond(now);

        ZonedDateTime localTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a z");

        String formattedTime = localTime.format(formatter);

        System.out.println("Epoch Seconds: " + now);
        System.out.println("Formatted Local Time: " + formattedTime);
    }
}
