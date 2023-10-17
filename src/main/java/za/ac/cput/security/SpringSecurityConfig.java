package za.ac.cput.security;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

    private final JwtAuthenticationFilter jwtAuthenticationFilter ;
    private final CustomerUserDetailsService customerUserDetailsService ;

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception
    { http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()

            //user endpoints
            //user registration endpoints
            .requestMatchers("/api/user/register").permitAll()
            .requestMatchers("/api/user/authenticate").permitAll()
            //user car endpoints
            .requestMatchers("/api/cars/list/*").permitAll()
            .requestMatchers("/api/cars/list/available/*").permitAll()
            .requestMatchers("/api/settings/read").permitAll()
            .requestMatchers("/api/user/profile/*").permitAll()
            //admin endpoints
            //admin cars endpoints
            .requestMatchers("/api/admin/cars/all").permitAll()
            .requestMatchers("/api/admin/cars/create").permitAll()
            .requestMatchers("/api/admin/cars/read/*").permitAll()
            .requestMatchers("/api/admin/cars/update/*/").permitAll()
            .requestMatchers("/api/admin/cars/delete/*").permitAll()
            //admin users endpoints
            .requestMatchers("/api/admin/users/list/*").permitAll()
            .requestMatchers("/api/admin/users/create").permitAll()
            .requestMatchers("/api/admin/users/read/*").permitAll()
            .requestMatchers("/api/admin/users/update/*").permitAll()
            .requestMatchers("/api/admin/users/update/*/*").permitAll()
            .requestMatchers("/api/admin/users/delete/*").permitAll()
            //admin settings endpoints
            .requestMatchers("/api/admin/settings/read").permitAll()
            .requestMatchers("/api/admin/settings/update").permitAll()
            //superadmin endpoints

         //   .requestMatchers("/api/admins/**").permitAll()
          //  .requestMatchers("/api/superadmin/**").permitAll()
          /*  .requestMatchers("/api/user/profile/**").hasAuthority("USER")
            .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
            .requestMatchers("/api/admins/**").hasAuthority("ADMIN")
            .requestMatchers("/api/superadmin/**").hasAuthority("SUPERADMIN") */;
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return  http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    { return authenticationConfiguration.getAuthenticationManager();}

    @Bean
    public PasswordEncoder passwordEncoder()
    { return new BCryptPasswordEncoder(); }

}
