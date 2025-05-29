package com.spring.fortress.vehicles.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class MyGenerator {

    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String CHASSIS_CHARS = "ABCDEFGHJKLMNPRZ0123456789"; // Excludes I, O, Q
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random 16-character alphanumeric national ID
     * @return String representing the national ID
     */
    public String generateNationalId() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generates a random car plate with format: XXX123X (3 letters, 3 numbers, 1 letter)
     * All letters are uppercase
     * @return String representing the car plate
     */
    public String generateCarPlate() {
        StringBuilder sb = new StringBuilder(7);

        // First 3 letters
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(LETTERS.length());
            sb.append(LETTERS.charAt(index));
        }

        // 3 numbers
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }

        // Last letter
        int index = random.nextInt(LETTERS.length());
        sb.append(LETTERS.charAt(index));

        return sb.toString();
    }

    /**
     * Generates a random 6-digit meter number
     * @return String representing the 6-digit meter number
     */
    public String generateMeterNumber() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generates a random 17-character chassis number
     * Complies with pattern ^[A-HJ-NPR-Z0-9]{17}$ (alphanumeric, excluding I, O, Q)
     * @return String representing the chassis number
     */
    public String generateChassisNumber() {
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < 17; i++) {
            int index = random.nextInt(CHASSIS_CHARS.length());
            sb.append(CHASSIS_CHARS.charAt(index));
        }
        return sb.toString();
    }
}