package com.spring.fortress.vehicles.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility for generating MySQL trigger definitions for the electricity token system.
 * Creates a trigger.sql file with triggers for token validation and auditing.
 *
 * @author Fortress Backend
 * @version 1.2
 * @since 1.0
 */
@Component
@Slf4j
public class SqlTriggerGenerator {

    private static final String OUTPUT_PATH1 = "src/main/resources/triggers.sql";
    private static final String OUTPUT_PATH2 = "/app/triggers.sql";

    /**
     * Generates a trigger.sql file containing MySQL triggers for token management.
     * Includes triggers for unit validation, TID uniqueness, expiration checks, and auditing.
     *
     * @throws RuntimeException if file writing fails
     */
    public void generateTriggerSql() {
        String triggerSql = """
        -- Trigger Definitions for Electricity Token System
        -- Version: 1.2
        -- Database: MySQL
        -- Generated by: Fortress Energy Solutions
        -- Date: 2025-05-16

        DELIMITER //

        -- Validates that token units are non-negative
        CREATE TRIGGER check_token_units
        BEFORE INSERT ON TOKEN
        FOR EACH ROW
        BEGIN
            IF NEW.units < 0 THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Units cannot be negative';
            END IF;
        END;
        //

        -- Ensures TID uniqueness for the same meter
        CREATE TRIGGER check_token_tid
        BEFORE INSERT ON TOKEN
        FOR EACH ROW
        BEGIN
            IF EXISTS (
                SELECT 1 FROM TOKEN t
                WHERE t.issued_at = NEW.issued_at
                AND t.meter_id = NEW.meter_id
            ) THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate TID detected';
            END IF;
        END;
        //

        -- Prevents insertion of already expired tokens
        CREATE TRIGGER flag_expired_tokens
        BEFORE INSERT ON TOKEN
        FOR EACH ROW
        BEGIN
            IF NEW.expires_at <= NOW() THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Token is already expired';
            END IF;
        END;
        //

        -- Updates meter balance after valid token insertion
        CREATE TRIGGER update_meter_balance
        AFTER INSERT ON TOKEN
        FOR EACH ROW
        BEGIN
            UPDATE METER
            SET balance = balance + NEW.units
            WHERE id = NEW.meter_id;
        END;
        //

        -- Logs token usage to TOKEN_AUDIT table
        CREATE TRIGGER log_token_usage
        AFTER INSERT ON TOKEN
        FOR EACH ROW
        BEGIN
            INSERT INTO TOKEN_AUDIT (token_value, meter_number, units, used_at, is_valid)
            SELECT NEW.token_value, m.meter_number, NEW.units, NOW(), TRUE
            FROM METER m
            WHERE m.id = NEW.meter_id;
        END;
        //

        DELIMITER ;
        """;

        try (FileWriter writer = new FileWriter(OUTPUT_PATH1)) {
            writer.write(triggerSql);
            log.info("Successfully generated {} with MySQL triggers for token management", OUTPUT_PATH1);
        } catch (IOException e) {
            log.error("Failed to generate {}: {}", OUTPUT_PATH2, e.getMessage(), e);
            throw new RuntimeException("Error generating triggers.sql", e);
        }
    }
}
