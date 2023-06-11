/*
package za.ac.cput.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
               */
/* .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/register").permitAll() // Allow registration endpoint
                        .requestMatchers("/login").permitAll() // Allow login endpoint
                        .requestMatchers("/api/**").permitAll() // Secure API endpoints
                        .requestMatchers(HttpMethod.POST, "/api/admin/cars/create").permitAll() // Allow car creation
                        .anyRequest().authenticated()
                )*//*

                .csrf().disable() // Disable CSRF for now (will be fixed later) this allows us to test with Postman
                .authorizeHttpRequests().anyRequest().permitAll()  // this will be changed to only allow certain requests to certain endpoints later on (e.g. only allow GET requests to /api/signup).
                .and()

                .httpBasic();
        return http.build();
    }

}
*/
