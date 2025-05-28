package com.spring.fortress;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main application class for the Fortress Application.
 * Configures and starts the Spring Boot application with JPA auditing and scheduling.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableJpaRepositories(
        basePackages = "com.spring.fortress.vehicles.repositories"
)
@Slf4j
public class FortressApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FortressApplication.class);
        Environment env = app.run(args).getEnvironment();
        String host = getHostAddress();
        String port = env.getProperty("server.port", "9090");
        String url = "http";
        String contextPath = env.getProperty("server.servlet.context-path", "/fortress/api/v1") + "/swagger-ui.html";
        String applicationUrl = String.format(url+"://%s:%s%s", host, port, contextPath);
        log.info("FortressApplication started at: {}", applicationUrl);
    }

    /**
     * Retrieves the host address for the application URL.
     *
     * @return the host address or localhost if unknown
     */
    private static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Unable to determine host address: {}. Falling back to localhost", e.getMessage());
            return "localhost";
        }
    }
}