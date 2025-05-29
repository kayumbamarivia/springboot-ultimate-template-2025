package com.spring.fortress.vehicles.services;

import com.spring.fortress.vehicles.config.JwtConfig;
import com.spring.fortress.vehicles.dtos.request.*;
import com.spring.fortress.vehicles.dtos.response.JwtTokenResponse;
import com.spring.fortress.vehicles.dtos.response.VerificationResponse;
import com.spring.fortress.vehicles.enums.Account;
import com.spring.fortress.vehicles.enums.Role;
import com.spring.fortress.vehicles.exceptions.VerificationException;
import com.spring.fortress.vehicles.interfaces.UserInterface;
import com.spring.fortress.vehicles.models.User;
import com.spring.fortress.vehicles.repositories.UserRepository;
import com.spring.fortress.vehicles.utils.JwtUtil;
import com.spring.fortress.vehicles.utils.VerificationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service for managing user registration, activation, verification, and login.
 * Handles user lifecycle with secure authentication and email notifications.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserInterface {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Password strength regex: at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Registers a new user with the provided details.
     * Checks for duplicate email and saves the user with an encoded password.
     *
     * @param request the user registration request
     * @return a success message indicating next steps
     * @throws IllegalStateException if the email is already registered
     */
    @Override
    @Transactional
    public String register(@Valid UserRequest request) {
        log.info("Registering user with email: {}", request.email());

        // Check for duplicate email
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email {} already registered", request.email());
            throw new IllegalStateException("Email already registered. Please use a different email or login");
        }

        // Create and save user entity
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .dob(request.dob())
                .role(Role.USER)
                .status(Account.INACTIVE)
                .nationalId(request.nationalId())
                .mobile(request.mobile())
                .build();

        userRepository.save(user);
        log.info("Successfully registered user with email: {}", request.email());

        return "Registration successful. Please activate your account using the activation API";
    }

    /**
     * Initiates account activation by sending a verification code to the user's email.
     *
     * @param email the email address of the user
     * @throws VerificationException if the user is not found
     */
    @Transactional
    public void activateAccount(String email) {
        log.info("Initiating account activation for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new VerificationException("No account found with email: " + email + ". Please sign up first");
                });

        // Generate verification code and expiration
        String verificationCode = VerificationUtil.generateVerificationCode();
        int otpExpirationMinutes = 10;
        LocalDateTime expirationTime = VerificationUtil.getExpirationTime(otpExpirationMinutes);

        // Update user with verification details and pending status
        user.setStatus(Account.PENDING);
        user.setVerificationCode(verificationCode);
        user.setCodeExpirationTime(expirationTime);
        userRepository.save(user);

        // Send activation email
        AccountActivationRequest activationRequest = new AccountActivationRequest(
                user.getEmail(),
                user.getFullName(),
                verificationCode,
                VerificationUtil.formatDateTime(expirationTime)
        );

        emailService.sendActivateAccountEmail(activationRequest);
        log.info("Activation email sent to: {}", email);
    }

    /**
     * Verifies a user's account using the provided verification code.
     * Activates the account if the code is valid and not expired.
     *
     * @param request the verification request
     * @return a success message
     * @throws VerificationException if verification fails
     */
    @Transactional
    public String verifyAccount(@Valid VerificationRequest request) {
        log.info("Verifying account for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("No account found with email: {}", request.email());
                    return new VerificationException("No account found with email: " + request.email() + ". Please sign up first");
                });

        // Validate verification code
        if (user.getVerificationCode() == null) {
            log.warn("No verification code found for email: {}", request.email());
            throw new VerificationException("Please request an activation code first");
        }

        if (VerificationUtil.isCodeExpired(user.getCodeExpirationTime())) {
            log.warn("Verification code expired for email: {}", request.email());
            emailService.sendOtpExpiredNotification(user.getEmail(), user.getFullName());
            throw new VerificationException("Verification code has expired. Please request a new one");
        }

        if (!user.getVerificationCode().equals(request.verificationCode())) {
            log.warn("Invalid verification code for email: {}", request.email());
            throw new VerificationException("Invalid verification code");
        }

        // Activate account and clear verification data
        user.setStatus(Account.ACTIVE);
        user.setVerificationCode(null);
        user.setCodeExpirationTime(null);
        userRepository.save(user);

        // Send confirmation email
        VerificationResponse response = new VerificationResponse(user.getEmail(), user.getFullName());
        emailService.sendAccountVerifiedSuccessfullyEmail(response);

        log.info("Successfully verified account for email: {}", request.email());
        return "Account verified successfully. You can now proceed to login";
    }

    /**
     * Resets a user's password using the provided email, verification code, and new password.
     * Validates the verification code and updates the password using the provided PasswordEncoder.
     *
     * @param email       the user's email address
     * @param verificationCode   the verification code sent to the user
     * @param newPassword the new password to set
     * @return a success message
     * @throws VerificationException if validation fails
     */
    @Override
    @Transactional
    public String resetPassword(String email, String verificationCode, String newPassword) {
        log.info("Processing password reset for email: {}", email);

        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            log.warn("Invalid email provided for password reset");
            throw new VerificationException("Email cannot be null or empty");
        }

        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            log.warn("Invalid verification code provided for email: {}", email);
            throw new VerificationException("Verification code cannot be null or empty");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            log.warn("New password is null or empty for email: {}", email);
            throw new VerificationException("New password cannot be null or empty");
        }

        // Validate password strength
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            log.warn("New password does not meet strength requirements for email: {}", email);
            throw new VerificationException(
                    "Password must be at least 8 characters long and include at least one uppercase letter, " +
                            "one lowercase letter, one digit, and one special character (@$!%*?&)"
            );
        }

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new VerificationException("No account found with email: " + email);
                });

        // Validate verification code
        if (user.getVerificationCode() == null) {
            throw new VerificationException("No password reset code found. Please request a password reset first");
        }

        if (VerificationUtil.isCodeExpired(user.getCodeExpirationTime())) {
            emailService.sendOtpExpiredNotification(user.getEmail(), user.getFullName());
            throw new VerificationException("Password reset code has expired. Please request a new one");
        }

        if (!user.getVerificationCode().equals(verificationCode)) {
            throw new VerificationException("Invalid verification code");
        }

        // Update password and clear verification data
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationCode(null);
        user.setCodeExpirationTime(null);
        userRepository.save(user);

        // Send confirmation email
        VerificationResponse response = new VerificationResponse(user.getEmail(), user.getFullName());
        emailService.sendPasswordResetSuccessfully(response);
        log.info("Successfully reset password for email: {}", email);

        return "Password reset successfully. You can now log in with your new password";
    }

    @Override
    public String changeRole(RoleRequest req) {
        // Find the user by email
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + req.email()));

        // Validate the role (optional but recommended)
        try {
            Role newRole = Role.valueOf(req.role().toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + req.role());
        }

        // Save the updated user to the database
        userRepository.save(user);

        return "User with email: " + req.email() + " now has role: " + req.role();
    }

    /**
     * Authenticates a user and issues a JWT token upon successful login.
     *
     * @param request the login request
     * @return a JWT token response
     * @throws IllegalStateException if authentication fails or account is inactive
     */
    @Override
    public JwtTokenResponse login(@Valid LoginRequest request) {
        log.info("Processing login for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Invalid email: {}", request.email());
                    return new IllegalStateException("Invalid email or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid password for email: {}", request.email());
            throw new IllegalStateException("Invalid email or password");
        }

        // Check account status
        if (user.getStatus() != Account.ACTIVE) {
            log.warn("Account not verified for email: {}", request.email());
            throw new IllegalStateException("Account not verified. Please activate your account first");
        }

        // Authenticate and generate JWT
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), request.password());
        authenticationManager.authenticate(auth);
        String token = JwtUtil.createToken(
                jwtConfig.getSecretKey(),
                user.getEmail(),
                jwtConfig.getIssuer(),
                jwtConfig.getExpiryInSeconds()
        );

        log.info("Successful login for email: {}", request.email());
        return new JwtTokenResponse(token);
    }
}