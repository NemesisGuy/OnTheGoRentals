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

    private final JwtAuthenticationFilter jwtAuthenticationFilter ;
    private final CustomerUserDetailsService customerUserDetailsService ;

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception
    { http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests()

            //user endpoints
            //user registration endpoints
            .requestMatchers("/api/user/register").permitAll()
            .requestMatchers("/api/user/authenticate").permitAll()
            //user settings endpoints
            .requestMatchers("/api/settings/read").permitAll()
            //user contact us endpoints
            .requestMatchers("/api/contactUs/create").permitAll()
            //user about us endpoints
            .requestMatchers("/api/aboutUs/read/*").permitAll()
            //user car endpoints
            .requestMatchers("/api/cars/list/*").permitAll()
            .requestMatchers("/api/cars/list/available/*").permitAll()

            .requestMatchers("/api/user/profile/*").hasAuthority("ADMIN")
            //admin endpoints
            //admin settings endpoints
            .requestMatchers("/api/admin/settings/read").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/settings/update").hasAuthority("ADMIN")

            //admin cars endpoints
            .requestMatchers("/api/admin/cars/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/cars/create").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/cars/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/cars/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/cars/delete/*").hasAuthority("ADMIN")
            //admin users endpoints
            .requestMatchers("/api/admin/users/list/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/users/create").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/users/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/users/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/users/update/*/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/users/delete/*").hasAuthority("ADMIN")
            //admin rentals endpoints
            .requestMatchers("/api/admin/rentals/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/rentals/list/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/rentals/create").hasAuthority("ADMIN")//issues there on front end
            .requestMatchers("/api/admin/rentals/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/rentals/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/rentals/delete/*").hasAuthority("ADMIN")
            //admin damage report endpoints
            .requestMatchers("/api/admin/damageReport/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/damageReport/create").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/damageReport/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/damageReport/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/damageReport/delete/*").hasAuthority("ADMIN")
            //admin contact us endpoints
            .requestMatchers("/api/admin/contactUs/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/contactUs/create").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/contactUs/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/contactUs/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/contactUs/delete/*").hasAuthority("ADMIN")
            //admin about us endpoints
            .requestMatchers("/api/admin/aboutUs/all").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/aboutUs/create").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/aboutUs/read/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/aboutUs/update/*").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/aboutUs/delete/*").hasAuthority("ADMIN")

            //admin faqs endpoints
            .requestMatchers("/api/admin/faq/get-all").permitAll()

            //admin settings endpoints
            .requestMatchers("/api/admin/settings/read").hasAuthority("ADMIN")
            .requestMatchers("/api/admin/settings/update").hasAuthority("ADMIN")

            //admins testing
            .requestMatchers("/api/admins/**").hasAuthority("ADMIN")
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
