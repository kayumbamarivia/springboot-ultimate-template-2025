package com.spring.fortress.vehicles.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Secure password encoder using Argon2 algorithm.
 * Configured with high security parameters for robust password hashing.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public class Argon2PasswordEncoderUtil implements PasswordEncoder {
    private static final Logger logger = LoggerFactory.getLogger(Argon2PasswordEncoderUtil.class);

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 8;
    private static final int ITERATIONS = 4;
    private static final int MEMORY = 1 << 16;
    private static final int PARALLELISM = 2;

    private final Argon2 argon2;

    /**
     * Constructs an Argon2 password encoder with default configuration.
     */
    public Argon2PasswordEncoderUtil() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);
        logger.info("Argon2PasswordEncoder initialized with iterations={}, memory={} KB, parallelism={}",
                ITERATIONS, MEMORY, PARALLELISM);
    }

    /**
     * Encodes a raw password using Argon2.
     *
     * @param rawPassword the password to encode
     * @return the encoded password
     * @throws IllegalArgumentException if the password is null
     */
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            logger.warn("Attempted to encode null password");
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            String hash = argon2.hash(ITERATIONS, MEMORY, PARALLELISM, rawPassword.toString());
            logger.debug("Password encoded successfully");
            return hash;
        } catch (Exception e) {
            logger.error("Error encoding password: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to encode password", e);
        }
    }

    /**
     * Verifies if a raw password matches an encoded password.
     *
     * @param rawPassword     the raw password to verify
     * @param encodedPassword the encoded password
     * @return {@code true} if the passwords match, {@code false} otherwise
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            logger.warn("Invalid input for password matching: rawPassword={}, encodedPassword={}",
                    rawPassword == null ? "null" : "non-null",
                    encodedPassword == null ? "null" : "non-null");
            return false;
        }
        try {
            boolean matches = argon2.verify(encodedPassword, rawPassword.toString());
            logger.debug("Password match result: {}", matches);
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying password: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify password", e);
        }
    }

    /**
     * Wipes sensitive data from memory.
     */
    public void wipe() {
        try {
            argon2.wipeArray((char[]) null);
            logger.debug("Argon2 memory wiped");
        } catch (Exception e) {
            logger.warn("Error during memory wipe: {}", e.getMessage(), e);
        }
    }
}