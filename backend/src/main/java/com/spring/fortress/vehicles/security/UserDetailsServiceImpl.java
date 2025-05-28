package com.spring.fortress.vehicles.security;

import com.spring.fortress.vehicles.models.User;
import com.spring.fortress.vehicles.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * <p>
 * This service is responsible for loading user details from the database
 * during authentication. It converts our application's User entity into
 * a Spring Security UserDetails object that can be used for authentication
 * and authorization.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Loads a user by their email address.
     * <p>
     * This method is called by Spring Security during authentication to retrieve
     * the user details based on the provided username (email in our case).
     * </p>
     *
     * @param email the user's email address
     * @return a UserDetails object containing the user's authentication information
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.info("Successfully loaded user: {}", email);

        // Convert to Spring Security's UserDetails
        return new MyUserPrincipal(user);
    }
}