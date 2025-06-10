package za.ac.cput.security;

/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()
/*
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
*/
/*
                        .ignoringRequestMatchers("/api/v1/auth/**") // Allow JWT endpoints to bypass CSRF
*/
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Preflight support
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public v1 endpoints
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/v1/about-us/**").permitAll()
                        .requestMatchers("/api/v1/cars/**").permitAll()
                        .requestMatchers("/api/v1/help-center/**").permitAll()
                        .requestMatchers("/api/v1/faq/**").permitAll()
                        .requestMatchers("/api/v1/contact-us").permitAll()
                        .requestMatchers("/api/v1/bookings/available-cars").permitAll()

                        // Authenticated v1 endpoints
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/users/me/**").authenticated()
                        .requestMatchers("/api/v1/files/cars/**").permitAll() // Allow public access to car images
                        .requestMatchers("/api/v1/files/**").authenticated() // Default for other files, was changed from permitAll
                        .requestMatchers("/api/v1/files/selfies/**").authenticated() // This is more specific, remains authenticated
                        .requestMatchers("/api/v1/rentals/from-booking/**").authenticated()
                        // Removed specific /api/v1/admin/... rules that were .authenticated()
                        // They will now be covered by the broader /api/v1/admin/** rule below.

                        // Admin v1 endpoints
                        // Ensure this rule covers all /api/v1/admin paths correctly.
                        // Specific admin sub-paths like /api/v1/admin/rentals/** are explicitly defined before or covered here.
                        .requestMatchers("/api/v1/admin/rentals/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Explicit rule, good
                        .requestMatchers("/api/v1/admin/**", "/api/v1/admins/**").hasAnyAuthority("ADMIN", "SUPERADMIN")

                        // Public static & framework paths
                        .requestMatchers("/public/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()

                        // Actuator endpoints (non-versioned)
                        .requestMatchers("/actuator/**", "/metrics", "/metrics/**").hasAnyAuthority("ADMIN", "SUPERADMIN") // Changed from permitAll

                        // Default deny
                        .anyRequest().authenticated()
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://otgr.nemesisnet.co.za"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
