package za.ac.cput.security;

/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        // User endpoints
                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/user/authenticate").permitAll()
                        .requestMatchers("/api/user/refresh").permitAll() // <<< ADD THIS
                        .requestMatchers("/api/user/logout").authenticated() // <<< ADD THIS (needs auth to identify user for logout)
                        .requestMatchers("/api/oauth2/google/login").permitAll() // Or /api/oauth2/google/code
                        .requestMatchers("/api/user/profile/*").authenticated()
                        .requestMatchers("/api/user/profile/*/*").authenticated()
                        .requestMatchers("/api/user/rentals/*").authenticated()
                        // User about and contact us endpoints
                        .requestMatchers("/api/aboutUs/read/*").permitAll()
                        .requestMatchers("/api/aboutUs/all").permitAll()
                        .requestMatchers("/api/aboutUs/latest").permitAll()
                        .requestMatchers("/api/contactUs/create").permitAll()
                        // User settings endpoints
                        .requestMatchers("/api/settings/read").permitAll()
                        // User car endpoints
                        .requestMatchers("/api/cars/**").permitAll()
                        // Help center and FAQ user endpoints
                        .requestMatchers("/api/faq/**").permitAll()
                        .requestMatchers("/api/help-center/**").permitAll()
                        .requestMatchers("/api/bookings/**").permitAll() // for dev purposes
                        ///actuator/prometheus
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        ///actuator/prometheus
                        .requestMatchers("/metrics").permitAll()
                        .requestMatchers("/metrics/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/admins/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("Authentication failed: " + authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Please log in again");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("Access denied: " + accessDeniedException.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden - You do not have permission to access this resource");
                        })
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
