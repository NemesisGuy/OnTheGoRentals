package za.ac.cput.config;
/**
 * CorsConfig.java
 * This is the configuration class for CORS, allowing the frontend to access the backend, and vice versa, without any issues.
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Access-Control-Allow-Origin")
                        .maxAge(3600);
                System.out.println("CorsConfig was triggered");

            }
        };
    }
}
