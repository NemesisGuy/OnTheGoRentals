package za.ac.cput.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/register").permitAll() // Allow registration endpoint
                        .requestMatchers("/login").permitAll() // Allow login endpoint
                        .anyRequest().permitAll()
                )
                .httpBasic();
        return http.build();
    }

}
