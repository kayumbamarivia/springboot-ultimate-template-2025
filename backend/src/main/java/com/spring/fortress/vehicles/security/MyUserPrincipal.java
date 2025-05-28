package com.spring.fortress.vehicles.security;

import com.spring.fortress.vehicles.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * <p>
 * This class adapts our application's User model to Spring Security's
 * UserDetails interface, providing the necessary authentication and
 * authorization information.
 * </p>
 *
 * @param user The user entity associated with this principal.
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 */
public record MyUserPrincipal(User user) implements UserDetails {

    /**
     * Returns the authorities (roles) granted to the user.
     * <p>
     * Converts the user's role into a Spring Security GrantedAuthority.
     * The role is prefixed with "ROLE_" as per Spring Security convention.
     * </p>
     *
     * @return the authorities granted to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     * <p>
     * In this implementation, the user's email is used as the username.
     * </p>
     *
     * @return the user's email as the username
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is valid (i.e. not expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Account expiration not implemented in this version
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Account locking not implemented in this version
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true if the user's credentials are valid (i.e. not expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credential expiration not implemented in this version
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled
     */
    @Override
    public boolean isEnabled() {
        // Only active accounts are enabled
        return user.getStatus() == com.spring.fortress.vehicles.enums.Account.ACTIVE;
    }
}