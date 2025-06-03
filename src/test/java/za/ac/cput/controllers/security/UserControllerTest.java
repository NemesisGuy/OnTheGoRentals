package za.ac.cput.controllers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
// Removed UserDetailsService import as it wasn't explicitly mocked/needed for this controller's context
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.dto.request.UserUpdateRequestDTO;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // Added for Optional return from userRepository
import java.util.Random; // Added for mock user ID generation
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static IUserService userServiceMock;
    private static IRentalService rentalServiceMock;
    private static JwtUtilities jwtUtilitiesMock;
    private static CustomerUserDetailsService customerUserDetailsServiceMock;
    private static IRoleRepository roleRepositoryMock;
    private static IUserRepository userRepositoryMock;
    private static PasswordEncoder passwordEncoderMock;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IUserService userService() {
            userServiceMock = mock(IUserService.class);
            return userServiceMock;
        }

        @Bean
        public IRentalService rentalService() {
            rentalServiceMock = mock(IRentalService.class);
            return rentalServiceMock;
        }

        @Bean
        public JwtUtilities jwtUtilities() {
            jwtUtilitiesMock = mock(JwtUtilities.class);
            return jwtUtilitiesMock;
        }

        @Bean
        public CustomerUserDetailsService customerUserDetailsService() {
            customerUserDetailsServiceMock = mock(CustomerUserDetailsService.class);
            return customerUserDetailsServiceMock;
        }

        @Bean
        public IRoleRepository roleRepository() {
            roleRepositoryMock = mock(IRoleRepository.class);
            return roleRepositoryMock;
        }

        @Bean
        public IUserRepository userRepository() {
            userRepositoryMock = mock(IUserRepository.class);
            return userRepositoryMock;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            passwordEncoderMock = mock(PasswordEncoder.class);
            return passwordEncoderMock;
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUserEntity;
    private String sampleUserEmail;
    private UUID sampleUserUuid;
    private Role sampleRoleUser;
    private Role sampleRoleAdmin;
    private Role sampleRoleSuperAdmin;

    @BeforeEach
    void setUp() {
        sampleUserEmail = "testuser@example.com";
        sampleUserUuid = UUID.randomUUID();

        sampleRoleUser = Role.builder().id(1).roleName(RoleName.USER).build();
        sampleRoleAdmin = Role.builder().id(2).roleName(RoleName.ADMIN).build();
        sampleRoleSuperAdmin = Role.builder().id(3).roleName(RoleName.SUPERADMIN).build();

        sampleUserEntity = User.builder()
                .id(1)
                .uuid(sampleUserUuid)
                .email(sampleUserEmail)
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .roles(List.of(sampleRoleUser))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(5))
                .deleted(false)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                sampleUserEmail,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reset mocks before each test
        reset(userServiceMock, rentalServiceMock, jwtUtilitiesMock, customerUserDetailsServiceMock,
                roleRepositoryMock, userRepositoryMock, passwordEncoderMock);

        // --- Stubbing for CommandLineRunner dependencies ---
        when(roleRepositoryMock.findByRoleName(RoleName.USER)).thenReturn(sampleRoleUser);
        when(roleRepositoryMock.findByRoleName(RoleName.ADMIN)).thenReturn(sampleRoleAdmin);
        when(roleRepositoryMock.findByRoleName(RoleName.SUPERADMIN)).thenReturn(sampleRoleSuperAdmin);

        when(userServiceMock.saveRole(any(Role.class))).thenAnswer(invocation -> {
            Role roleToSave = invocation.getArgument(0);
            int mockId = switch (roleToSave.getRoleNameEnum()) {
                case USER -> 1;
                case ADMIN -> 2;
                case SUPERADMIN -> 3;
                default -> 99;
            };
            return Role.builder()
                    .id(roleToSave.getId() != null ? roleToSave.getId() : mockId)
                    .roleName(roleToSave.getRoleNameEnum())
                    .build();
        });

        // Default behavior for user existence checks by CommandLineRunner
        // Specific emails can be overridden in tests if CommandLineRunner logic depends on it
        when(userRepositoryMock.findByEmail(endsWith("@gmail.com"))).thenReturn(Optional.empty());


        when(userServiceMock.createUser(any(User.class), anyList())).thenAnswer(invocation -> {
            User userToCreate = invocation.getArgument(0);
            List<Role> roles = invocation.getArgument(1);
            // Ensure the returned User has an ID and UUID, as the CommandLineRunner might log them
            return User.builder()
                    .id(userToCreate.getId() != null ? userToCreate.getId() : new Random().nextInt(1000) + 100) // Assign mock ID
                    .uuid(userToCreate.getUuid() != null ? userToCreate.getUuid() : UUID.randomUUID())
                    .firstName(userToCreate.getFirstName())
                    .lastName(userToCreate.getLastName())
                    .email(userToCreate.getEmail())
                    .password("encodedMockPasswordForRunner")
                    .roles(roles)
                    .authProvider(userToCreate.getAuthProvider() != null ? userToCreate.getAuthProvider() : za.ac.cput.domain.enums.AuthProvider.LOCAL)
                    .deleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        });
        when(userServiceMock.saveUser(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            // Ensure saved user has necessary fields if CommandLineRunner re-checks it
            User user = userToSave.toBuilder().id(userToSave.getId() != null ? userToSave.getId() : new Random().nextInt(1000) + 200)
                    .uuid(userToSave.getUuid() != null ? userToSave.getUuid() : UUID.randomUUID())
                    .build();
            return user;
        });
        when(passwordEncoderMock.encode(anyString())).thenReturn("encodedMockPasswordForRunner");
    }

    // --- Test getCurrentUserProfile ---
    @Test
    void getCurrentUserProfile_shouldReturnUserProfile_whenUserExists() throws Exception {
        when(userServiceMock.read(sampleUserEmail)).thenReturn(sampleUserEntity);

        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(sampleUserEmail)))
                .andExpect(jsonPath("$.firstName", is(sampleUserEntity.getFirstName())))
                .andExpect(jsonPath("$.uuid", is(sampleUserUuid.toString())));

        verify(userServiceMock).read(sampleUserEmail);
    }

    @Test
    void getCurrentUserProfile_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userServiceMock.read(sampleUserEmail)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User profile not found for authenticated user: " + sampleUserEmail)));

        verify(userServiceMock).read(sampleUserEmail);
    }

    // --- Test updateCurrentUserProfile ---
    @Test
    void updateCurrentUserProfile_shouldUpdateAndReturnProfile_whenValidRequest() throws Exception {
        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO("UpdatedFirst", "UpdatedLast");
        // Create a distinct object for the "read" operation to avoid side effects if sampleUserEntity is modified by mistake.
        User originalUserEntityForRead = sampleUserEntity.toBuilder().build();


        User updatedUserEntityFromService = User.builder()
                .id(sampleUserEntity.getId()).uuid(sampleUserEntity.getUuid())
                .email(sampleUserEntity.getEmail()).firstName(updateDTO.getFirstName())
                .lastName(updateDTO.getLastName()).password(sampleUserEntity.getPassword())
                .roles(sampleUserEntity.getRoles()).createdAt(sampleUserEntity.getCreatedAt())
                .updatedAt(LocalDateTime.now()).deleted(sampleUserEntity.isDeleted())
                .build();

        when(userServiceMock.read(sampleUserEmail)).thenReturn(originalUserEntityForRead);
        // For the update method, the service receives the ID and the entity containing updates.
        // The entity passed to update will be a modified version of originalUserEntityForRead.
        when(userServiceMock.update(eq(originalUserEntityForRead.getId()), any(User.class)))
                .thenReturn(updatedUserEntityFromService);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(updateDTO.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updateDTO.getLastName())));

        verify(userServiceMock).read(sampleUserEmail);
        verify(userServiceMock).update(eq(originalUserEntityForRead.getId()), argThat(userArg ->
                userArg.getFirstName().equals(updateDTO.getFirstName()) &&
                        userArg.getLastName().equals(updateDTO.getLastName()) &&
                        userArg.getEmail().equals(originalUserEntityForRead.getEmail()) // Verify email is not changed
        ));
    }

    @Test
    void updateCurrentUserProfile_shouldReturnCurrentProfile_whenNoChangesInDTO() throws Exception {
        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO(sampleUserEntity.getFirstName(), sampleUserEntity.getLastName());
        when(userServiceMock.read(sampleUserEmail)).thenReturn(sampleUserEntity);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(sampleUserEntity.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(sampleUserEntity.getLastName())));

        verify(userServiceMock).read(sampleUserEmail);
        verify(userServiceMock, never()).update(anyInt(), any(User.class));
    }


    @Test
    void updateCurrentUserProfile_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO("UpdatedFirst", "UpdatedLast");
        when(userServiceMock.read(sampleUserEmail)).thenReturn(null);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found for update: " + sampleUserEmail)));

        verify(userServiceMock).read(sampleUserEmail);
        verify(userServiceMock, never()).update(anyInt(), any(User.class));
    }

    // --- Test getCurrentUserRentalHistory ---
    @Test
    void getCurrentUserRentalHistory_shouldReturnRentalList_whenHistoryExists() throws Exception {
        Rental rental1 = Rental.builder().setId(1).setUuid(UUID.randomUUID()).build();
        Rental rental2 = Rental.builder().setId(2).setUuid(UUID.randomUUID()).build();
        List<Rental> rentalHistory = List.of(rental1, rental2);

        when(userServiceMock.read(sampleUserEmail)).thenReturn(sampleUserEntity);
        when(rentalServiceMock.getRentalHistoryByUser(argThat(user -> user.getUuid().equals(sampleUserUuid)))).thenReturn(rentalHistory);

        mockMvc.perform(get("/api/v1/users/me/rental-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid", is(rental1.getUuid().toString())));

        verify(userServiceMock).read(sampleUserEmail);
        verify(rentalServiceMock).getRentalHistoryByUser(argThat(userArg -> userArg.getUuid().equals(sampleUserUuid)));
    }

    @Test
    void getCurrentUserRentalHistory_shouldReturnNoContent_whenNoHistory() throws Exception {
        when(userServiceMock.read(sampleUserEmail)).thenReturn(sampleUserEntity);
        when(rentalServiceMock.getRentalHistoryByUser(any(User.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/me/rental-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userServiceMock).read(sampleUserEmail);
        verify(rentalServiceMock).getRentalHistoryByUser(any(User.class));
    }

    @Test
    void getCurrentUserRentalHistory_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userServiceMock.read(sampleUserEmail)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/me/rental-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found for retrieving rental history: " + sampleUserEmail)));

        verify(userServiceMock).read(sampleUserEmail);
        verify(rentalServiceMock, never()).getRentalHistoryByUser(any(User.class));
    }
}