package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils; // For setting @Value fields
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRefreshTokenRepository;
import za.ac.cput.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RefreshTokenServiceImpl}.
 * Tests the lifecycle management of refresh tokens, including creation,
 * verification, and deletion, with mocked repository dependencies.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-30
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private final Long refreshTokenDurationMs = 604800000L; // 7 days, example value

    private User sampleUser;
    private RefreshToken sampleRefreshToken;

    @BeforeEach
    void setUp() {
        // Set the @Value field using ReflectionTestUtils
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshTokenDurationMs);

        sampleUser = User.builder().id(1).email("test@example.com").uuid(UUID.randomUUID()).deleted(false).build();

        sampleRefreshToken = new RefreshToken();
        sampleRefreshToken.setId(1L);
        sampleRefreshToken.setUser(sampleUser);
        sampleRefreshToken.setToken(UUID.randomUUID().toString());
        sampleRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    }

    // --- findByToken Tests ---
    @Test
    void findByToken_shouldReturnToken_whenTokenExists() {
        String tokenString = sampleRefreshToken.getToken();
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(sampleRefreshToken));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(tokenString);

        assertTrue(foundToken.isPresent());
        assertEquals(sampleRefreshToken, foundToken.get());
        verify(refreshTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    void findByToken_shouldReturnEmptyOptional_whenTokenDoesNotExist() {
        String tokenString = "nonExistentToken";
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(tokenString);

        assertFalse(foundToken.isPresent());
    }

    // --- createRefreshToken Tests ---
    @Test
    void createRefreshToken_shouldCreateNewToken_whenUserExistsAndNoExistingToken() {
        when(userRepository.findByIdAndDeletedFalse(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(refreshTokenRepository.findByUser(sampleUser)).thenReturn(Optional.empty());
        // Capture the argument passed to save to verify its properties
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken tokenToSave = invocation.getArgument(0);
            tokenToSave.setId(2L); // Simulate DB setting ID
            return tokenToSave;
        });

        RefreshToken createdToken = refreshTokenService.createRefreshToken(sampleUser.getId());

        assertNotNull(createdToken);
        assertNotNull(createdToken.getId());
        assertEquals(sampleUser, createdToken.getUser());
        assertNotNull(createdToken.getToken());
        assertTrue(createdToken.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_shouldUpdateExistingToken_whenUserExistsAndTokenExists() {
        String oldTokenString = sampleRefreshToken.getToken();
        Instant oldExpiry = sampleRefreshToken.getExpiryDate();

        when(userRepository.findByIdAndDeletedFalse(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(refreshTokenRepository.findByUser(sampleUser)).thenReturn(Optional.of(sampleRefreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken updatedToken = refreshTokenService.createRefreshToken(sampleUser.getId());

        assertNotNull(updatedToken);
        assertEquals(sampleRefreshToken.getId(), updatedToken.getId()); // ID should be the same
        assertEquals(sampleUser, updatedToken.getUser());
        assertNotEquals(oldTokenString, updatedToken.getToken(), "Token string should be rotated (new UUID)");
        assertTrue(updatedToken.getExpiryDate().isAfter(oldExpiry), "New expiry date should be later");
        assertTrue(updatedToken.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository, times(1)).save(sampleRefreshToken); // Verifies the existing token object was saved
    }

    @Test
    void createRefreshToken_shouldThrowResourceNotFoundException_whenUserNotFound() {
        Integer nonExistentUserId = 99;
        when(userRepository.findByIdAndDeletedFalse(nonExistentUserId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            refreshTokenService.createRefreshToken(nonExistentUserId);
        });
        assertEquals("User not found with id: " + nonExistentUserId + " while creating refresh token.", exception.getMessage());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    // --- verifyDeviceAndExpiration Tests ---
    @Test
    void verifyDeviceAndExpiration_shouldReturnToken_whenTokenIsValidAndNotExpired() {
        // sampleRefreshToken is set up to be not expired in @BeforeEach
        RefreshToken verifiedToken = refreshTokenService.verifyDeviceAndExpiration(sampleRefreshToken);

        assertNotNull(verifiedToken);
        assertEquals(sampleRefreshToken, verifiedToken);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class)); // Delete should not be called
    }

    @Test
    void verifyDeviceAndExpiration_shouldThrowTokenRefreshExceptionAndDeletingToken_whenTokenIsExpired() {
        sampleRefreshToken.setExpiryDate(Instant.now().minusSeconds(3600)); // Set expiry to 1 hour ago
        doNothing().when(refreshTokenRepository).delete(sampleRefreshToken); // Mock deletion

        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyDeviceAndExpiration(sampleRefreshToken);
        });

        assertTrue(exception.getMessage().contains("Refresh token was expired."));
        verify(refreshTokenRepository, times(1)).delete(sampleRefreshToken);
    }

    // --- deleteByUserId Tests ---
    @Test
    void deleteByUserId_shouldDeleteTokensForUser_whenUserExists() {
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(refreshTokenRepository.deleteByUser(sampleUser)).thenReturn(1); // Simulate 1 token deleted

        assertDoesNotThrow(() -> refreshTokenService.deleteByUserId(sampleUser.getId()));

        verify(refreshTokenRepository, times(1)).deleteByUser(sampleUser);
    }

    @Test
    void deleteByUserId_shouldThrowResourceNotFoundException_whenUserNotFoundForDeletion() {
        Integer nonExistentUserId = 99;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            refreshTokenService.deleteByUserId(nonExistentUserId);
        });
        assertTrue(exception.getMessage().contains("User not found with id: " + nonExistentUserId));
        verify(refreshTokenRepository, never()).deleteByUser(any(User.class));
    }

    // --- deleteByUser Tests ---
    @Test
    void deleteByUser_shouldDeleteTokensForUser() {
        when(refreshTokenRepository.deleteByUser(sampleUser)).thenReturn(1);

        refreshTokenService.deleteByUser(sampleUser);

        verify(refreshTokenRepository, times(1)).deleteByUser(sampleUser);
    }

    @Test
    void deleteByUser_shouldDoNothing_whenUserIsNull() {
        refreshTokenService.deleteByUser(null);
        verify(refreshTokenRepository, never()).deleteByUser(any());
    }


    // --- deleteByToken Tests ---
    @Test
    void deleteByToken_shouldCallRepositoryDeleteByToken() {
        String tokenString = sampleRefreshToken.getToken();
        // Assuming deleteByToken in repository returns void or an int we don't check here
        doNothing().when(refreshTokenRepository).deleteByToken(tokenString); // if void
        // when(refreshTokenRepository.deleteByToken(tokenString)).thenReturn(1); // if returns int

        refreshTokenService.deleteByToken(tokenString);

        verify(refreshTokenRepository, times(1)).deleteByToken(tokenString);
    }
}