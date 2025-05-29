package com.spring.fortress.vehicles.interfaces;

import com.spring.fortress.vehicles.dtos.request.LoginRequest;
import com.spring.fortress.vehicles.dtos.request.RoleRequest;
import com.spring.fortress.vehicles.dtos.request.UserRequest;
import com.spring.fortress.vehicles.dtos.request.VerificationRequest;
import com.spring.fortress.vehicles.dtos.response.JwtTokenResponse;
import jakarta.validation.Valid;

/**
 * Interface defining user-related operations in the vehicle tracking system.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public interface UserInterface {

    /**
     * Authenticates a user and issues a JWT token.
     *
     * @param request the login request
     * @return a JWT token response
     * @throws IllegalStateException if authentication fails or account is inactive
     */
    JwtTokenResponse login(@Valid LoginRequest request);

    /**
     * Registers a new user with the provided details.
     *
     * @param request the user registration request
     * @return a success message indicating next steps
     * @throws IllegalStateException if the email is already registered
     */
    String register(@Valid UserRequest request);

    /**
     * Initiates account activation by sending a verification code.
     *
     * @param email the email address of the user
     * @throws IllegalStateException if the user is not found
     */
    void activateAccount(String email);

    /**
     * Verifies a user's account using a verification code.
     *
     * @param request the verification request
     * @return a success message
     * @throws IllegalStateException if verification fails
     */
    String verifyAccount(@Valid VerificationRequest request);

    /**
     * Resets a user's password using a reset code.
     *
     * @param email      the email address of the user
     * @param resetCode  the reset code sent to the user
     * @param newPassword the new password
     * @return a success message
     * @throws IllegalStateException if the reset code is invalid or user is not found
     */
    String resetPassword(String email, String resetCode, String newPassword);

    String changeRole(RoleRequest re);
}