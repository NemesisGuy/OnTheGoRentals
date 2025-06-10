package za.ac.cput.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthServiceImpl}.
 * Tests authentication-related logic including registration, login, token refresh, and logout.
 * All external dependencies are mocked.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-30
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserService userService;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtilities jwtUtilities;
    @Mock
    private IRefreshTokenService refreshTokenService;
    @Mock
    private HttpServletResponse httpServletResponse; // For cookie setting
    @Mock
    private Authentication authentication; // For mocking Authentication object

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Role sampleRoleUser;
    private RefreshToken sampleRefreshToken;

    // Values to mimic those from @Value annotations
    private final Long refreshTokenDurationMs = 604800000L; // 7 days
    private final String refreshTokenCookieName = "myAppRefreshToken";
    private final String refreshTokenCookiePath = "/api/v1/auth";
    private final boolean secureCookie = true;


    @BeforeEach
    void setUp() {
        // Manually set @Value fields for the authService instance as @InjectMocks doesn't handle them
        // This can be done using reflection or by making them package-private and setting directly for tests
        // For simplicity in this example, we'll assume they could be set if needed, or that the
        // generateHttpOnlyRefreshTokenCookie method in AuthServiceImpl is robust enough to use defaults if values are null
        // (which it isn't currently, it would NPE).
        // A better way is to use ReflectionTestUtils or make service constructor take these values.
        // For now, we will mock what generateHttpOnlyRefreshTokenCookie depends on.
        // Let's refine this slightly by injecting the values via constructor if possible, or reflection if not.
        // As a workaround for direct @Value field injection in tests without Spring context:
        authService = new AuthServiceImpl(userService, roleRepository, passwordEncoder, authenticationManager, jwtUtilities, refreshTokenService);
        // Now use reflection to set private @Value fields:
        try {
            java.lang.reflect.Field durationField = AuthServiceImpl.class.getDeclaredField("refreshTokenDurationMs");
            durationField.setAccessible(true);
            durationField.set(authService, refreshTokenDurationMs);

            java.lang.reflect.Field nameField = AuthServiceImpl.class.getDeclaredField("refreshTokenCookieName");
            nameField.setAccessible(true);
            nameField.set(authService, refreshTokenCookieName);

            java.lang.reflect.Field pathField = AuthServiceImpl.class.getDeclaredField("refreshTokenCookiePath");
            pathField.setAccessible(true);
            pathField.set(authService, refreshTokenCookiePath);

            java.lang.reflect.Field secureField = AuthServiceImpl.class.getDeclaredField("secureCookie");
            secureField.setAccessible(true);
            secureField.set(authService, secureCookie);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set @Value fields for test", e);
        }


        sampleRoleUser = new Role(RoleName.USER);
        sampleRoleUser.setId(1);

        sampleUser = User.builder()
                .id(1)
                .uuid(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword") // Assume already encoded for some tests
                .roles(Collections.singletonList(sampleRoleUser))
                .build();

        sampleRefreshToken = new RefreshToken();
        sampleRefreshToken.setId(1L);
        sampleRefreshToken.setUser(sampleUser);
        sampleRefreshToken.setToken(UUID.randomUUID().toString());
        sampleRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        SecurityContextHolder.clearContext(); // Ensure clean context for each test
    }

    // --- registerUser Tests ---
    // In AuthServiceImplTest.java
    @Test
    void registerUser_shouldCreateAndReturnUser_whenEmailIsUniqueAndRoleExists() {
        String email = "new@example.com";
        String plainPassword = "password123";
        // String encodedPassword = "encodedPassword123"; // No longer directly asserted in AuthDetails from this method

        User userDetailsFromAuthService = User.builder() // This is what AuthServiceImpl builds before calling userService.createUser
                .firstName("New")
                .lastName("User")
                .email(email)
                .password(plainPassword) // AuthService sends plain password
                .build();

        // Mock what userService.read returns (for email check)
        when(userService.read(email)).thenReturn(null);
        // Mock what roleRepository returns
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(sampleRoleUser);

        // Mock what userService.createUser returns
        // This mock now simulates that userService.createUser DOES encode the password
        // and correctly sets roles, ID, UUID, etc.
        when(userService.createUser(any(User.class), eq(Collections.singletonList(sampleRoleUser))))
                .thenAnswer(invocation -> {
                    User userArgPassedToCreate = invocation.getArgument(0);
                    List<Role> rolesArgPassedToCreate = invocation.getArgument(1);
                    return User.builder()
                            .id(2) // Simulated persisted ID
                            .uuid(UUID.randomUUID()) // Simulated UUID
                            .firstName(userArgPassedToCreate.getFirstName())
                            .lastName(userArgPassedToCreate.getLastName())
                            .email(userArgPassedToCreate.getEmail())
                            .password("password-was-encoded-by-user-service") // Simulate encoded pass
                            .roles(rolesArgPassedToCreate)
                            .deleted(false)
                            .authProvider(userArgPassedToCreate.getAuthProvider() != null ? userArgPassedToCreate.getAuthProvider() : AuthProvider.LOCAL)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                });

        // Act
        User registeredUser = authService.registerUser("New", "User", email, plainPassword, RoleName.USER);

        // Assert (on what authService.registerUser returns, which is the result of userService.createUser)
        assertNotNull(registeredUser);
        assertEquals(2, registeredUser.getId());
        assertEquals(email, registeredUser.getEmail());
        assertEquals("password-was-encoded-by-user-service", registeredUser.getPassword()); // Check the mocked encoded password
        assertNotNull(registeredUser.getUuid());
        assertFalse(registeredUser.isDeleted());
        assertTrue(registeredUser.getRoles().contains(sampleRoleUser));

        // Verify interactions
        verify(userService, times(1)).read(email);
        verify(roleRepository, times(1)).findByRoleName(RoleName.USER);
        // Verify that userService.createUser was called with the User object containing the PLAIN password
        // (because UserServiceImpl.createUser is responsible for encoding)
        verify(userService, times(1)).createUser(
                argThat(user ->
                        user.getEmail().equals(email) &&
                                user.getFirstName().equals("New") &&
                                user.getPassword().equals(plainPassword) // <<< Verify plain password was passed
                ),
                eq(Collections.singletonList(sampleRoleUser))
        );
    }


    @Test
    void registerUser_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
        String email = "existing@example.com";
        when(userService.read(email)).thenReturn(sampleUser); // Simulate email already exists

        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.registerUser("Fail", "User", email, "password", RoleName.USER);
        });
        verify(roleRepository, never()).findByRoleName(any(RoleName.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void registerUser_shouldThrowIllegalStateException_whenDefaultRoleNotFound() {
        String email = "new@example.com";
        when(userService.read(email)).thenReturn(null);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(null); // Role not found

        assertThrows(IllegalStateException.class, () -> {
            authService.registerUser("New", "User", email, "password", RoleName.USER);
        });
    }

    // --- loginUser Tests ---
    @Test
    void loginUser_shouldReturnAuthDetailsAndSetCookie_onSuccessfulAuthentication() {
        String email = "test@example.com";
        String plainPassword = "password123";
        String accessToken = "mockAccessToken";
        String refreshTokenString = "mockRefreshTokenString";

        sampleRefreshToken.setToken(refreshTokenString); // Ensure it has a token string

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, plainPassword)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userService.read(email)).thenReturn(sampleUser);
        when(jwtUtilities.generateToken(eq(sampleUser), anyList())).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(sampleUser.getId())).thenReturn(sampleRefreshToken);

        AuthServiceImpl.AuthDetails authDetails = authService.loginUser(email, plainPassword, httpServletResponse);

        assertNotNull(authDetails);
        assertEquals(accessToken, authDetails.getAccessToken());
        assertEquals(sampleUser, authDetails.getUser());
        assertTrue(authDetails.getRoleNames().contains(RoleName.USER.name()));
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        verify(httpServletResponse, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    void loginUser_shouldThrowBadCredentialsException_onAuthenticationFailure() {
        String email = "test@example.com";
        String plainPassword = "wrongPassword";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.loginUser(email, plainPassword, httpServletResponse);
        });
        verify(httpServletResponse, never()).addHeader(anyString(), anyString());
    }

    // --- refreshAccessToken Tests ---
    @Test
    void refreshAccessToken_shouldReturnNewTokenAndSetCookie_whenRefreshTokenIsValid() {
        String oldRefreshTokenString = sampleRefreshToken.getToken();
        String newAccessToken = "newMockAccessToken";
        RefreshToken newDbRefreshToken = new RefreshToken();
        newDbRefreshToken.setToken("newRefreshTokenStringOnDb");

        when(refreshTokenService.findByToken(oldRefreshTokenString)).thenReturn(Optional.of(sampleRefreshToken));
        when(refreshTokenService.verifyDeviceAndExpiration(sampleRefreshToken)).thenReturn(sampleRefreshToken);
        when(refreshTokenService.createRefreshToken(sampleUser.getId())).thenReturn(newDbRefreshToken);
        when(jwtUtilities.generateToken(eq(sampleUser), anyList())).thenReturn(newAccessToken);
        doNothing().when(refreshTokenService).deleteByToken(oldRefreshTokenString);


        AuthServiceImpl.RefreshedTokenDetails refreshedDetails = authService.refreshAccessToken(oldRefreshTokenString, httpServletResponse);

        assertNotNull(refreshedDetails);
        assertEquals(newAccessToken, refreshedDetails.getNewAccessToken());

        verify(refreshTokenService, times(1)).deleteByToken(oldRefreshTokenString);
        verify(refreshTokenService, times(1)).createRefreshToken(sampleUser.getId());
        verify(httpServletResponse, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), contains(newDbRefreshToken.getToken()));
    }

    @Test
    void refreshAccessToken_shouldThrowTokenRefreshException_whenTokenNotFound() {
        String invalidToken = "invalidOrNonExistentToken";
        when(refreshTokenService.findByToken(invalidToken)).thenReturn(Optional.empty());

        assertThrows(TokenRefreshException.class, () -> {
            authService.refreshAccessToken(invalidToken, httpServletResponse);
        });
    }

    @Test
    void refreshAccessToken_shouldThrowTokenRefreshException_whenTokenIsExpired() {
        String expiredTokenString = "expiredTokenString";
        sampleRefreshToken.setToken(expiredTokenString);
        // Simulate verify throwing the exception
        when(refreshTokenService.findByToken(expiredTokenString)).thenReturn(Optional.of(sampleRefreshToken));
        when(refreshTokenService.verifyDeviceAndExpiration(sampleRefreshToken))
                .thenThrow(new TokenRefreshException(expiredTokenString, "Refresh token was expired."));

        assertThrows(TokenRefreshException.class, () -> {
            authService.refreshAccessToken(expiredTokenString, httpServletResponse);
        });
    }

    // --- logoutUser Tests ---
    @Test
    void logoutUser_shouldClearContextAndDeleteTokensAndCookie_whenUserExists() {
        doNothing().when(refreshTokenService).deleteByUserId(sampleUser.getId());
        // Mocking getCleanHttpOnlyRefreshTokenCookie implicitly through clearAuthCookies

        boolean result = authService.logoutUser(sampleUser.getId(), httpServletResponse);

        assertTrue(result);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Security context should be cleared");
        verify(refreshTokenService, times(1)).deleteByUserId(sampleUser.getId());
        verify(httpServletResponse, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString()); // Verifies clearAuthCookies was effectively called
    }

    // --- clearAuthCookies Test ---
    @Test
    void clearAuthCookies_shouldAddHeaderToClearCookie() {
        authService.clearAuthCookies(httpServletResponse);
        // We verify that addHeader was called with a cookie string that has Max-Age=0
        verify(httpServletResponse, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), argThat(cookieString ->
                cookieString.contains(refreshTokenCookieName + "=;") && // Empty value
                        cookieString.contains("Max-Age=0") &&
                        cookieString.contains("Path=" + refreshTokenCookiePath) &&
                        cookieString.contains("HttpOnly") &&
                        (secureCookie ? cookieString.contains("Secure") : !cookieString.contains("Secure")) &&
                        cookieString.contains("SameSite=Lax")
        ));
    }
}