package com.spring.fortress.vehicles.config;
import com.spring.fortress.vehicles.security.SecuringClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Configuration class for application security components.
 * <p>
 * This class defines Spring beans for security-related components in the application.
 * It registers the SecuringClass that provides the core security configuration.
 * </p>
 *
 * @author Fortress Backend
 * @version 1.0
 * @since 1.0
 * @see com.spring.fortress.vehicles.security.SecuringClass
 */
@Configuration
@EnableWebSecurity
public class ConfigurationClass {

    /**
     * Creates and registers the main security configuration bean.
     * <p>
     * This bean defines the security filter chain, authentication manager,
     * password encoder, and other security-related components.
     * </p>
     *
     * @return the security configuration instance
     */
    @Bean
    public SecuringClass securingClass() {
        return new SecuringClass();
    }
}