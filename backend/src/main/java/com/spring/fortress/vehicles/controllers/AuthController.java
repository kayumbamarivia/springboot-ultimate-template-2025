package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.dtos.request.LoginRequest;
import com.spring.fortress.vehicles.dtos.request.ResetPasswordRequest;
import com.spring.fortress.vehicles.dtos.request.UserRequest;
import com.spring.fortress.vehicles.dtos.request.VerificationRequest;
import com.spring.fortress.vehicles.dtos.response.ErrorResponse;
import com.spring.fortress.vehicles.dtos.response.JwtTokenResponse;
import com.spring.fortress.vehicles.services.EmailService;
import com.spring.fortress.vehicles.services.UserService;
import com.spring.fortress.vehicles.utils.VerificationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing authentication and user-related operations.
 * Provides endpoints for registration, activation, verification, login, and password reset.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@Tag(name = "Auth", description = "Endpoints for authentication and user management")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    /**
     * Registers a new user.
     *
     * @param request the user registration request
     * @return a response with the registration result
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRequest request) {
        log.info("Processing user registration for email: {}", request.email());
        String result = userService.register(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Initiates account activation by sending a verification code.
     *
     * @param body the request body containing the email
     * @return a response indicating the activation status
     */
    @Operation(summary = "Initiate account activation", description = "Sends a verification code to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/activate")
    public ResponseEntity<String> activateAccount(@Valid @RequestBody Map<String, String> body) {
        String email = body.get("email");
        log.info("Initiating account activation for email: {}", email);
        userService.activateAccount(email);
        return ResponseEntity.ok("Verification code sent to your email. Please verify your account.");
    }

    /**
     * Verifies a user account using a verification code.
     *
     * @param request the verification request
     * @return a response with the verification result
     */
    @Operation(summary = "Verify a user account", description = "Verifies a user account using a provided OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account verified successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid verification data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/verify")
    public ResponseEntity<String> verifyAccount(@Valid @RequestBody VerificationRequest request) {
        log.info("Verifying account for email: {}", request.email());
        String result = userService.verifyAccount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Authenticates a user and issues a JWT token.
     *
     * @param request the login request
     * @return a response with the JWT token
     */
    @Operation(summary = "Authenticate a user", description = "Logs in a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Processing login for email: {}", request.email());
        JwtTokenResponse result = userService.login(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Requests a password reset code.
     *
     * @param body the request body containing email and full name
     * @return a response indicating the reset code request status
     */
    @Operation(summary = "Request password reset code", description = "Sends a password reset code to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset code sent successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/password-reset-code")
    public ResponseEntity<String> passwordResetCode(@Valid @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String fullName = body.get("fullName");
        log.info("Requesting password reset code for email: {}", email);
        String resetCode = VerificationUtil.generateVerificationCode();
        emailService.sendResetPasswordMail(new ResetPasswordRequest(email, fullName, resetCode));
        return ResponseEntity.ok("Password reset code sent to your email");
    }

    /**
     * Resets a user's password using a reset code.
     *
     * @param body the request body containing email, reset code, and new password
     * @return a response indicating the reset status
     */
    @Operation(summary = "Reset user password", description = "Resets the user's password using a reset code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid reset data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/password-reset")
    public ResponseEntity<String> passwordReset(@Valid @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String resetCode = body.get("resetCode");
        String newPassword = body.get("newPassword");
        log.info("Processing password reset for email: {}", email);
        // Placeholder: Implement password reset logic in UserService
        throw new UnsupportedOperationException("Password reset not implemented");
    }

    @GetMapping("test")
    public String test(){
        return "Accessed!";
    }
}