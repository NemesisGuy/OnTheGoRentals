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
import za.ac.cput.security.oauth.CustomOAuth2UserService;
import za.ac.cput.security.oauth.OAuth2AuthenticationSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SpringSecurityConfig.java
 * Main security configuration for the application.
 * Defines the security filter chain, including JWT and OAuth2 login flows.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2025-06-23
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // --- NEW: Inject the custom OAuth2 handlers ---
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

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
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Allow preflight OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Define all public endpoints, including the new OAuth2 endpoints
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/oauth2/**", // <-- Important for the OAuth2 flow
                                "/api/v1/cars/**",
                                "/api/v1/files/**",
                                "/api/v1/about-us/**",
                                "/api/v1/help-center/**",
                                "/api/v1/faq/**",
                                "/api/v1/contact-us",
                                "/api/v1/bookings/available-cars",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/metrics",
                                "/metrics/**"
                        ).permitAll()

                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                        .anyRequest().authenticated()
                )
                // --- START: NEW OAUTH2 LOGIN CONFIGURATION ---
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // Step 1: Use our custom service to load user info
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler) // Step 2: Use our custom handler on successful login
                )
                // --- END: NEW OAUTH2 LOGIN CONFIGURATION ---
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
}