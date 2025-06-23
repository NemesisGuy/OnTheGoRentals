package za.ac.cput.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SpringSecurityConfig.java
 * Main security configuration for the application.
 * Defines the security filter chain, authentication manager, and password encoder.
 * CORS (Cross-Origin Resource Sharing) is configured to be handled by an upstream
 * reverse proxy (e.g., Nginx, Cloudflare) and is intentionally NOT configured here
 * to prevent header conflicts.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-10
 */
@Configuration
@EnableWebSecurity
/*
@EnableMethodSecurity // Enables method-level security like @PreAuthorize
*/
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the main security filter chain for the application.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // This tells Spring to apply its default CORS filter. However, since we are
                // removing the CorsConfigurationSource bean below, this filter will have
                // no configuration and will not add any CORS headers. This is the desired behavior.
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Allow preflight OPTIONS requests to pass through security filters
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Define all public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/cars/**").permitAll()
                        .requestMatchers("/api/v1/files/**").permitAll()
                        .requestMatchers("/api/v1/about-us/**").permitAll()
                        .requestMatchers("/api/v1/help-center/**").permitAll()
                        .requestMatchers("/api/v1/faq/**").permitAll()
                        .requestMatchers("/api/v1/contact-us").permitAll()
                        .requestMatchers("/api/v1/bookings/available-cars").permitAll()
                        // 🔓 Permit Swagger endpoints
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()


                        .requestMatchers("/actuator/**", "/metrics", "/metrics/**").permitAll()

                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Admin endpoints require specific roles
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                );

        // Add the custom JWT filter before the standard username/password filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides the AuthenticationManager bean, required for the authentication process.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides the PasswordEncoder bean, using BCrypt for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * The CorsConfigurationSource bean has been REMOVED.
     * This action delegates all CORS header management to the upstream reverse proxy
     * (e.g., Nginx, Traefik, or a cloud load balancer), which is the recommended
     * practice for production environments to avoid conflicts and centralize configuration.
     */
}