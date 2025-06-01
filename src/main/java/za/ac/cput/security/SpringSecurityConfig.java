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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration

                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                // Permit OPTIONS requests globally for preflight
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
                                .requestMatchers("/api/v1/about-us").permitAll()
                                .requestMatchers("/api/v1/about-us/*").permitAll()
                                .requestMatchers("/api/aboutUs/read/*").permitAll()
                                .requestMatchers("/api/aboutUs/all").permitAll()
                                .requestMatchers("/api/aboutUs/latest").permitAll()
                                .requestMatchers("/api/contactUs/create").permitAll()
                                // User settings endpoints
                                .requestMatchers("/api/settings/read").permitAll()
                                // User car endpoints
                                .requestMatchers("/api/v1/cars").permitAll()
                                .requestMatchers("/api/v1/cars/**").permitAll()
                                // Help center and FAQ user endpoints
                                .requestMatchers("/api/faq/**").permitAll()
                                .requestMatchers("/api/help-center/**").permitAll()

                                // User booking endpoints
                                .requestMatchers("/api/bookings/**").permitAll() // for dev purposes
                                .requestMatchers("/api/v1/bookings").permitAll() // for dev purposes
                                .requestMatchers("/api/v1/bookings/**").permitAll() // for dev purposes
                                ///actuator/prometheus
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/actuator/prometheus").permitAll()
                                ///actuator/prometheus
                                .requestMatchers("/metrics").permitAll()
                                .requestMatchers("/metrics/**").permitAll()

                                // Admin endpoints
                                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                                .requestMatchers("/api/admins/**").hasAnyAuthority("ADMIN", "SUPERADMIN")


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                // User endpoints V1
                                // Authentication Endpoints (Public)
                                .requestMatchers("/api/v1/auth/register").permitAll()
                                .requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/auth/refresh").permitAll()

                                // Authentication Endpoints (Authenticated)
                                .requestMatchers("/api/v1/auth/logout").authenticated()

                                // Current User's Profile & Data (Authenticated)
                                .requestMatchers("/api/v1/users/me/**").authenticated()

                                // help center and FAQ user endpoints
                                .requestMatchers("/api/v1/help-center/**").permitAll()
                                .requestMatchers("/api/v1/faq/**").permitAll()
                                .requestMatchers("/api/v1/contact-us").permitAll()
                                // Admin endpoints
                                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                                .requestMatchers("/api/v1/admins/**").hasAnyAuthority("ADMIN", "SUPERADMIN")

                                // Deny all other requests by default if not explicitly permitted
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
        // Define your allowed origins EXPLICITLY
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",          // For local Vue dev (if this is your port)
                "https://otgr.nemesisnet.co.za"   // Your production frontend
                // Add other origins if needed (e.g., staging)
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
        configuration.setExposedHeaders(Arrays.asList( // If you have custom headers client needs to read
                "Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
                // "Set-Cookie" might not need to be explicitly exposed if handled by browser correctly
        ));
        configuration.setAllowCredentials(true); // Crucial for cookies
        configuration.setMaxAge(3600L); // How long the results of a preflight request can be cached

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }
}
