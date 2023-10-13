package za.ac.cput.config;
/**
 * CorsConfig.java
 * This is the configuration class for CORS, allowing the frontend to access the backend, and vice versa, without any issues.
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;


    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        /*.allowedOrigins("http://localhost:5173")*/
                       // .allowedOrigins(allowedOrigins) //user this for when locking down the allowed origins
                        .allowedOriginPatterns(allowedOrigins) //use this for when allowing all origins
                       // .allowedOrigins("http://localhost:5173", "http://192.168.0.105:5173", "http://192.168.56.1:5173", "http://172.31.112.1:5173", "http://192.0.0.0/8:5173", "http://192.169.0.101:5173")
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
