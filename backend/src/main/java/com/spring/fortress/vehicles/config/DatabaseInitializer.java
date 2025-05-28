package com.spring.fortress.vehicles.config;

import com.spring.fortress.vehicles.utils.SqlTriggerGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component responsible for generating SQL trigger scripts on application startup.
 * This class only generates the trigger SQL file but does NOT execute it on the DB.
 * You can then manually copy and run the generated SQL on your MySQL server.
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final SqlTriggerGenerator triggerGenerator;

    @Override
    public void run(String... args) {
        log.info("Generating trigger SQL script...");
        try {
            // Just generate the triggers.sql file, no DB execution
            triggerGenerator.generateTriggerSql();
            log.info("Trigger SQL script generated successfully. Please run it manually on your MySQL server.");
        } catch (Exception e) {
            log.error("Failed to generate trigger SQL script", e);
        }
    }
}
