// src/main/java/za/ac/cput/BackendApplication.java
package za.ac.cput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    /**
     * Main method to start the Spring Boot application.
     * This method initializes the application context and starts the embedded server.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("BackendApplication started successfully.");
        // Log the application startup
        /// Note that the DefaultDataInitializer will run automatically on application startup,it will initialize default roles and users
    }
}