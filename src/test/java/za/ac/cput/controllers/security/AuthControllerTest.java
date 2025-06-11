/*
package za.ac.cput.controllers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.security.SpringSecurityConfig;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.impl.AuthServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import(SpringSecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuthService authService;

    @MockBean
    private JwtUtilities jwtUtilities;

    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.expiration:3600000}") // Example value
    private Long accessTokenExpirationMs;

    @Value("${app.security.refresh-cookie.name:appRefreshToken}")
    private String refreshTokenCookieName;

    private User sampleUser;
    private Role sampleRoleUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        sampleRoleUser = Role.builder().id(1).roleName(RoleName.USER).build();
        sampleUser = User.builder()
                .id(1).uuid(UUID.randomUUID()).firstName("Test").lastName("User")
                .email("test@example.com").password("encodedPassword")
                .roles(Collections.singletonList(sampleRoleUser))
                .authProvider(za.ac.cput.domain.enums.AuthProvider.LOCAL)
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(jwtUtilities.validateToken(anyString())).thenReturn(true);
        when(jwtUtilities.extractUserEmail(anyString())).thenReturn(sampleUser.getEmail());

        when(customerUserDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        sampleUser.getEmail(), sampleUser.getPassword(), sampleUser.getAuthorities()
                ));
    }

    private void mockSecurityContext(User userPrincipal) {
        List<SimpleGrantedAuthority> authorities = userPrincipal.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleNameEnum().name()))
                .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void register_shouldRegisterAndLoginUser_whenGivenValidDetails() throws Exception {
        RegisterDto registerDto = new RegisterDto("Test", "User", "newuser@example.com", "password123");
        User registeredUser = User.builder().id(2).firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName()).email(registerDto.getEmail())
                .password("encodedPassword").roles(Collections.singletonList(sampleRoleUser)).build();
        AuthServiceImpl.AuthDetails authDetails = new AuthServiceImpl.AuthDetails(
                registeredUser, "mockAccessToken", new ArrayList<>(Set.of("USER")));

        when(authService.registerUser(anyString(), anyString(), eq(registerDto.getEmail()), anyString(), eq(RoleName.USER)))
                .thenReturn(registeredUser);
        when(authService.loginUser(eq(registeredUser.getEmail()), eq(registerDto.getPassword()), any()))
                .thenReturn(authDetails);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken", is("mockAccessToken")))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.email", is(registeredUser.getEmail()))) // Corrected: userEmail to email
                .andExpect(jsonPath("$.data.roles[0]", is("USER")))
                .andExpect(jsonPath("$.data.accessTokenExpiresIn", is(accessTokenExpirationMs.intValue()))); // Corrected: expiresIn to accessTokenExpiresIn
    }

    @Test
    void login_shouldAuthenticateAndReturnTokens_whenCredentialsAreValid() throws Exception {
        LoginDto loginDto = new LoginDto("test@example.com", "password123");
        AuthServiceImpl.AuthDetails authDetails = new AuthServiceImpl.AuthDetails(
                sampleUser, "mockLoginAccessToken", new ArrayList<>(Set.of("USER")));
        when(authService.loginUser(eq(loginDto.getEmail()), eq(loginDto.getPassword()), any()))
                .thenReturn(authDetails);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", is("mockLoginAccessToken")))
                .andExpect(jsonPath("$.data.email", is(sampleUser.getEmail()))); // Corrected: userEmail to email
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() throws Exception {
        String mockOldRefreshToken = "validMockRefreshToken";
        AuthServiceImpl.RefreshedTokenDetails refreshedTokenDetails = new AuthServiceImpl.RefreshedTokenDetails("newMockAccessToken");
        when(authService.refreshAccessToken(eq(mockOldRefreshToken), any())).thenReturn(refreshedTokenDetails);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie(refreshTokenCookieName, mockOldRefreshToken)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", is("newMockAccessToken")))
                .andExpect(jsonPath("$.data.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.data.accessTokenExpiresIn", is(accessTokenExpirationMs.intValue()))); // Corrected: expiresIn to accessTokenExpiresIn
    }

    @Test
    void refreshToken_shouldReturnUnauthorized_whenRefreshTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").isEmpty())
                .andExpect(jsonPath("$.status", is("success")));
        verify(authService, never()).refreshAccessToken(anyString(), any());
    }

    @Test
    void logout_shouldClearSessionAndCookies_whenUserIsAuthenticated() throws Exception {
        mockSecurityContext(sampleUser);
        when(authService.logoutUser(eq(sampleUser.getId()), any())).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("Logout successful.")));
        verify(authService).logoutUser(eq(sampleUser.getId()), any());
    }

    @Test
    void logout_shouldReturnUnauthorized_whenUserIsGuest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unauthorized - Please log in again"));
        verify(authService, never()).clearAuthCookies(any());
        verify(authService, never()).logoutUser(anyInt(), any());
    }
}*/
