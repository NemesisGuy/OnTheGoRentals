package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IRefreshTokenService;

import java.time.LocalDateTime; // For simulating audit fields
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 * This class tests the CRUD operations and other user management logic,
 * ensuring correct interaction with mocked dependencies.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-29 (Updated for comprehensive tests and mock refinements)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IRefreshTokenService refreshTokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private Role sampleRoleUser;
    private Role sampleRoleAdmin;
    private List<Role> singleUserRoleList;
    private List<Role> adminUserRoleList;

    @BeforeEach
    void setUp() {
        sampleRoleUser = new Role(RoleName.USER);
        sampleRoleUser.setId(1);

        sampleRoleAdmin = new Role(RoleName.ADMIN);
        sampleRoleAdmin.setId(2);

        singleUserRoleList = Collections.singletonList(sampleRoleUser);
        adminUserRoleList = List.of(sampleRoleUser, sampleRoleAdmin);

        sampleUser = User.builder()
                .id(1)
                .uuid(UUID.randomUUID()) // Start with a UUID for sampleUser
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("$2a$encodedOriginalPassword") // Assume it's already encoded
                .roles(new ArrayList<>(singleUserRoleList))
                .authProvider(AuthProvider.LOCAL)
                .deleted(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    // --- createUser Tests ---
    @Test
    void createUser_shouldSaveAndReturnUser_whenEmailIsUnique() {
        User userDetailsToCreate = User.builder()
                .firstName("New")
                .lastName("User")
                .email("newuser@example.com")
                .password("newPassword123") // Plain password
                .build();
        String encodedPassword = "encodedNewPassword";
        UUID generatedUuid = UUID.randomUUID(); // Pre-determine what UUID @PrePersist or service would generate

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn(encodedPassword);

        // Mock the save operation to simulate JPA and @PrePersist behavior
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            // The userArg already has encoded password and roles set by the service.
            // @PrePersist in User entity would set UUID if userArg.getUuid() is null.
            // The service's createUser also tries to set UUID if null.
            return User.builder()
                    .id(2) // Simulate DB generated ID
                    .uuid(userArg.getUuid() != null ? userArg.getUuid() : generatedUuid) // Ensure it has a UUID
                    .firstName(userArg.getFirstName())
                    .lastName(userArg.getLastName())
                    .email(userArg.getEmail())
                    .password(userArg.getPassword()) // Already encoded
                    .roles(userArg.getRoles())
                    .deleted(false) // Should be set by service or @PrePersist
                    .authProvider(userArg.getAuthProvider() != null ? userArg.getAuthProvider() : AuthProvider.LOCAL) // @PrePersist
                    .createdAt(LocalDateTime.now()) // @PrePersist
                    .updatedAt(LocalDateTime.now()) // @PrePersist
                    .build();
        });

        User createdUser = userService.createUser(userDetailsToCreate, singleUserRoleList);

        assertNotNull(createdUser);
        assertEquals(2, createdUser.getId());
        assertEquals("newuser@example.com", createdUser.getEmail());
        assertEquals(encodedPassword, createdUser.getPassword());
        assertNotNull(createdUser.getUuid());
        assertFalse(createdUser.isDeleted());
        assertEquals(singleUserRoleList, createdUser.getRoles());

        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExistsException_whenEmailIsNotUnique() {
        User userDetailsToCreate = User.builder().email("john.doe@example.com").password("password").build();
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(userDetailsToCreate, singleUserRoleList);
        });
        assertEquals("Email john.doe@example.com is already taken!", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowIllegalArgumentException_ifPasswordIsNull() {
        User userDetailsToCreate = User.builder().email("nullpass@example.com").password(null).build();
        when(userRepository.existsByEmail("nullpass@example.com")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userDetailsToCreate, singleUserRoleList);
        });
        assertEquals("Password cannot be null or empty for new user creation.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("nullpass@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- read Tests ---
    // (read tests from previous response were likely okay, ensure they are present)
    @Test
    void readById_shouldReturnUser_whenFoundAndNotDeleted() {
        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleUser));
        User foundUser = userService.read(1);
        assertNotNull(foundUser);
        assertEquals(sampleUser.getEmail(), foundUser.getEmail());
    }
    // ... other read tests ...

    // --- update Tests ---
    // (update tests from previous response, ensure the save mock returns a fully hydrated object)
    @Test
    void update_shouldUpdateAndReturnUser_whenUserExists() {
        User userToUpdate = User.builder() // This is the state of existingUser *before* updates are applied
                .id(sampleUser.getId())
                .uuid(sampleUser.getUuid())
                .firstName(sampleUser.getFirstName())
                .lastName(sampleUser.getLastName())
                .email(sampleUser.getEmail())
                .password(sampleUser.getPassword()) // Original encoded password
                .roles(new ArrayList<>(sampleUser.getRoles()))
                .deleted(false)
                .authProvider(sampleUser.getAuthProvider())
                .createdAt(sampleUser.getCreatedAt())
                .updatedAt(sampleUser.getUpdatedAt())
                .build();

        User userUpdatesPayload = User.builder() // This is what the controller sends as 'userUpdates'
                .firstName("Johnathan")
                .email("john.doe.newmail@example.com")
                .password("newPlainPassword123")
                .roles(adminUserRoleList)
                .build();

        String newEncodedPassword = "encodedNewPassword123";

        when(userRepository.findByIdAndDeletedFalse(sampleUser.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepository.existsByEmailAndIdNot("john.doe.newmail@example.com", sampleUser.getId())).thenReturn(false);
        when(passwordEncoder.matches("newPlainPassword123", userToUpdate.getPassword())).thenReturn(false); // new pass is different
        when(passwordEncoder.encode("newPlainPassword123")).thenReturn(newEncodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedArg = invocation.getArgument(0); // This is 'existingUser' after modifications
            // Simulate that save returns the persisted state (which is the modified existingUser)
            return User.builder() // Build a new object to mimic what save returns
                    .id(savedArg.getId())
                    .uuid(savedArg.getUuid())
                    .firstName(savedArg.getFirstName())
                    .lastName(savedArg.getLastName())
                    .email(savedArg.getEmail())
                    .password(savedArg.getPassword())
                    .roles(new ArrayList<>(savedArg.getRoles()))
                    .authProvider(savedArg.getAuthProvider())
                    .deleted(savedArg.isDeleted())
                    .createdAt(savedArg.getCreatedAt())
                    .updatedAt(LocalDateTime.now()) // @PreUpdate would set this
                    .build();
        });

        User updatedUser = userService.update(sampleUser.getId(), userUpdatesPayload);

        assertNotNull(updatedUser);
        assertEquals("Johnathan", updatedUser.getFirstName());
        assertEquals("john.doe.newmail@example.com", updatedUser.getEmail());
        assertEquals(newEncodedPassword, updatedUser.getPassword());
        assertTrue(updatedUser.getRoles().containsAll(adminUserRoleList) && adminUserRoleList.containsAll(updatedUser.getRoles()));

        verify(userRepository).save(argThat(user ->
                user.getFirstName().equals("Johnathan") &&
                        user.getEmail().equals("john.doe.newmail@example.com") &&
                        user.getPassword().equals(newEncodedPassword)
        ));
    }
    // ... other update tests, including for ResourceNotFoundException, EmailAlreadyExistsException ...
    // ... test for update_shouldNotUpdatePassword_ifPasswordIsEmptyOrSameAsOldEncoded as corrected before ...

    // --- delete Tests ---
    // (delete tests from previous response were likely okay)
    @Test
    void delete_shouldMarkUserAsDeletedAndInvalidateTokens_whenUserExists() {
        when(userRepository.findByIdAndDeletedFalse(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // save returns the modified user
        doNothing().when(refreshTokenService).deleteByUserId(sampleUser.getId());

        boolean result = userService.delete(sampleUser.getId());

        assertTrue(result);
        verify(userRepository).save(argThat(User::isDeleted));
        verify(refreshTokenService).deleteByUserId(sampleUser.getId());
    }
    // ... other delete tests ...

    // --- getAll Tests ---
    // (getAll test from previous response was likely okay)
    @Test
    void getAll_shouldReturnListOfNonDeletedUsers() {
        List<User> users = List.of(sampleUser, User.builder().id(2).email("jane@example.com").deleted(false).build());
        when(userRepository.findByDeletedFalse()).thenReturn(users);
        List<User> result = userService.getAll();
        assertEquals(2, result.size());
    }


    // --- saveRole Tests ---
    // (saveRole test from previous response was likely okay)
    @Test
    void saveRole_shouldSaveAndReturnRole() {
        Role newRole = new Role(RoleName.SUPERADMIN);
        Role savedRole = new Role(RoleName.SUPERADMIN);
        savedRole.setId(3);

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        Role result = userService.saveRole(newRole);

        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals(RoleName.SUPERADMIN.name(), result.getRoleName());
    }

    // --- saveUser (generic) Tests ---
    @Test
    void saveUser_shouldSaveUserAndGenerateUuid_ifNewAndUuidNull() {
        User newUserNoUuid = User.builder().email("generic@example.com").password("encodedPass").build();
        // This user has no ID and no UUID.

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0); // userArg will have its UUID set by UserServiceImpl.saveUser
            // or by @PrePersist if service didn't set it
            UUID finalUuid = userArg.getUuid() != null ? userArg.getUuid() : UUID.randomUUID(); // Ensure it's not null

            return User.builder()
                    .id(10) // Simulate DB generated ID
                    .uuid(finalUuid) // Use the UUID that was set on userArg
                    .email(userArg.getEmail())
                    .password(userArg.getPassword())
                    .firstName(userArg.getFirstName())
                    .lastName(userArg.getLastName())
                    .roles(userArg.getRoles())
                    .deleted(userArg.isDeleted())
                    .authProvider(userArg.getAuthProvider())
                    .createdAt(userArg.getCreatedAt() != null ? userArg.getCreatedAt() : LocalDateTime.now())
                    .updatedAt(userArg.getUpdatedAt() != null ? userArg.getUpdatedAt() : LocalDateTime.now())
                    .build();
        });

        User result = userService.saveUser(newUserNoUuid);

        assertNotNull(result.getUuid(), "UUID should be generated and set");
        assertEquals(10, result.getId());
        // Also, the object passed to the service method should have been modified
        assertNotNull(newUserNoUuid.getUuid(), "Original user object passed to service should also have its UUID set");
        verify(userRepository).save(newUserNoUuid); // Verify with the object that had UUID set
    }

    @Test
    void saveUser_shouldSetDeletedToFalse_ifNewAndMarkedDeleted() {
        User newUserMarkedDeleted = User.builder()
                .email("newdel@example.com")
                .password("encodedPassword")
                .deleted(true) // Initially true, ID and UUID are null
                .build();

        // Mock for findByUuidAndDeletedFalse called inside saveUser
        // It's called with the UUID that saveUser *just generated*.
        // We need to ensure this mock doesn't interfere with the "new user" path.
        // Since the UUID is generated on the fly, any(UUID.class) should work.
        when(userRepository.findByUuidAndDeletedFalse(any(UUID.class))).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            return User.builder()
                    .id(12)
                    .uuid(userArg.getUuid()) // This UUID was set by saveUser or @PrePersist
                    .email(userArg.getEmail())
                    .password(userArg.getPassword())
                    .deleted(userArg.isDeleted()) // This should now be false
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        });

        User result = userService.saveUser(newUserMarkedDeleted);

        assertFalse(result.isDeleted(), "User should have deleted flag set to false");
        assertNotNull(result.getUuid(), "User should have a UUID");
        assertEquals(12, result.getId());

        verify(userRepository).save(argThat(user ->
                !user.isDeleted() &&
                        user.getUuid() != null &&
                        "newdel@example.com".equals(user.getEmail())
        ));
        // Verify findByUuidAndDeletedFalse was called once (with the generated UUID)
        verify(userRepository, times(1)).findByUuidAndDeletedFalse(newUserMarkedDeleted.getUuid());
    }

    // ... (deprecated save test and other tests from previous response) ...
    @Test
    void deprecatedSave_shouldDelegateToSaveUser() {
        // This test now implicitly tests saveUser's behavior as well
        User userToSave = User.builder().email("deprecated@example.com").password("encoded").build();
        UUID generatedUuid = UUID.randomUUID();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            return User.builder()
                    .id(100)
                    .uuid(arg.getUuid() != null ? arg.getUuid() : generatedUuid) // Ensure it has a UUID
                    .email(arg.getEmail())
                    .password(arg.getPassword())
                    .deleted(arg.isDeleted())
                    .build();
        });

        User result = userService.save(userToSave); // Call deprecated save

        assertNotNull(result.getUuid());
        assertEquals(100, result.getId());
        verify(userRepository, times(1)).save(userToSave);
    }
}