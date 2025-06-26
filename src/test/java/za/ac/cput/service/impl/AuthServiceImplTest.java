package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.IEmailService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthServiceImpl class.
 * These tests use Mockito to isolate the service logic from its dependencies (e.g., repositories, other services).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    // Mock all the dependencies required by AuthServiceImpl
    @Mock
    private IUserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IEmailService emailService;

    // Create an instance of the class we are testing and inject the mocks into it
    @InjectMocks
    private AuthServiceImpl authService;

    // Use this method to set up common test data or configurations before each test
    @BeforeEach
    void setUp() {
        // Use ReflectionTestUtils to set the value of the @Value-annotated frontendUrl field,
        // as it's not injected via the constructor in this test setup.
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:5173");
    }

    @Test
    @DisplayName("Should successfully register a new user and send a welcome email")
    void registerUser_WhenEmailIsNew_ShouldSucceed() {
        // --- Arrange ---
        // 1. Define test data
        String email = "newuser@example.com";
        Role userRole = new Role(1, RoleName.USER);
        User userToRegister = User.builder().firstName("John").lastName("Doe").email(email).password("password123").build();
        User savedUser = User.builder().id(1).firstName("John").email(email).build();

        // 2. Define the behavior of our mocks
        when(userService.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(userRole);
        when(userService.createUser(any(User.class), anyList())).thenReturn(savedUser);
        // We use doNothing() for void methods like sendHtmlMessage to ensure they are called without error.
        doNothing().when(emailService).sendHtmlMessage(anyString(), anyString(), anyString(), any(Map.class));

        // --- Act ---
        // 3. Call the method we are testing
        User result = authService.registerUser("John", "Doe", email, "password123", RoleName.USER);

        // --- Assert ---
        // 4. Verify the results and interactions
        assertNotNull(result);
        assertEquals(email, result.getEmail());

        // Verify that the dependencies were called the correct number of times
        verify(userService, times(1)).existsByEmail(email);
        verify(userService, times(1)).createUser(any(User.class), anyList());
        verify(emailService, times(1)).sendHtmlMessage(
                eq(email),
                eq("Welcome to On The Go Rentals!"),
                eq("email/welcome"),
                anyMap()
        );
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when registering with a duplicate email")
    void registerUser_WhenEmailExists_ShouldThrowException() {
        // --- Arrange ---
        String email = "existinguser@example.com";
        when(userService.existsByEmail(email)).thenReturn(true);

        // --- Act & Assert ---
        // Assert that calling the method throws the expected exception
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.registerUser("Jane", "Doe", email, "password123", RoleName.USER);
        });

        // Verify that downstream methods were NOT called
        verify(userService, never()).createUser(any(), any());
        verify(emailService, never()).sendHtmlMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should initiate password reset and send email when user exists")
    void initiatePasswordReset_WhenUserExists_ShouldGenerateTokenAndSendEmail() {
        // --- Arrange ---
        String email = "test@example.com";
        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setFirstName("Test");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // --- Act ---
        authService.initiatePasswordReset(email);

        // --- Assert ---
        // Verify that the user object was modified with a token and expiry
        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiry());

        // Verify that the email service was called correctly
        verify(emailService, times(1)).sendHtmlMessage(
                eq(email),
                eq("Your Password Reset Request"),
                eq("email/password-reset-request"),
                anyMap()
        );
    }

    @Test
    @DisplayName("Should do nothing when initiating password reset for a non-existent user")
    void initiatePasswordReset_WhenUserDoesNotExist_ShouldDoNothing() {
        // --- Arrange ---
        String email = "nouser@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // --- Act ---
        authService.initiatePasswordReset(email);

        // --- Assert ---
        // Verify that no save operations or email sends occurred
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendHtmlMessage(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should finalize password reset for a valid, non-expired token")
    void finalizePasswordReset_WithValidToken_ShouldSucceed() {
        // --- Arrange ---
        String token = "valid-token";
        String newPassword = "newPassword123";
        String encodedPassword = "encodedNewPassword";
        User user = new User();
        user.setId(1);
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // --- Act ---
        authService.finalizePasswordReset(token, newPassword);

        // --- Assert ---
        // Verify that the password was updated and the token was cleared
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(encodedPassword, user.getPassword());
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetTokenExpiry());
    }

    @Test
    @DisplayName("Should throw BadRequestException for an expired token")
    void finalizePasswordReset_WithExpiredToken_ShouldThrowException() {
        // --- Arrange ---
        String token = "expired-token";
        User user = new User();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1)); // Token expired an hour ago

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        // --- Act & Assert ---
        assertThrows(BadRequestException.class, () -> {
            authService.finalizePasswordReset(token, "any-password");
        });

        // Verify the expired token was cleared from the user object as part of the process
        verify(userRepository, times(1)).save(user);
        assertNull(user.getPasswordResetToken());
    }

    @Test
    @DisplayName("Should throw BadRequestException for an invalid token")
    void finalizePasswordReset_WithInvalidToken_ShouldThrowException() {
        // --- Arrange ---
        String token = "invalid-token";
        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        assertThrows(BadRequestException.class, () -> {
            authService.finalizePasswordReset(token, "any-password");
        });

        // Verify no save operation occurred
        verify(userRepository, never()).save(any());
    }
}