package za.ac.cput;
/**
 * BackendApplication.java
 * This is the main class for the backend
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import za.ac.cput.service.impl.ICarServiceImpl;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public ICarServiceImpl carService() {
        return new ICarServiceImpl();
    }

    //factory, repos, domain, controllers and services
}
