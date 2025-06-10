// src/test/java/za/ac/cput/DefaultDataInitializerTest.java
package za.ac.cput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.service.IUserService;
import za.ac.cput.utils.DefaultDataInitializer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyList; // Not needed if captor is used for list
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BackendApplication.class)
@ExtendWith(MockitoExtension.class)
class DefaultDataInitializerTest {

    @MockBean
    private IUserService userService;
    @MockBean
    private IRoleRepository roleRepository;
    @MockBean
    private IUserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private DefaultDataInitializer defaultDataInitializer;

    // Captors for createUser
    @Captor
    private ArgumentCaptor<User> userArgCaptorForCreate; // Renamed for clarity
    @Captor
    private ArgumentCaptor<List<Role>> rolesListCaptorForCreate; // Renamed for clarity

    // Captor for saveUser
    @Captor
    private ArgumentCaptor<User> userArgCaptorForSave; // Renamed for clarity

    // Captor for saveRole (if needed for specific assertions on Role object, otherwise any(Role.class) is fine for verify)
    @Captor
    private ArgumentCaptor<Role> roleArgCaptorForSaveRole;


    private Role userRoleEntity;
    private Role adminRoleEntity;
    private Role superAdminRoleEntity;

    @BeforeEach
    void setUpCommonMocks() {
        userRoleEntity = Role.builder().id(1).roleName(RoleName.USER).build();
        adminRoleEntity = Role.builder().id(2).roleName(RoleName.ADMIN).build();
        superAdminRoleEntity = Role.builder().id(3).roleName(RoleName.SUPERADMIN).build();

        reset(userService, roleRepository, userRepository, passwordEncoder); // Keep SpyBean defaultDataInitializer as is

        System.out.println("TEST --- DefaultDataInitializerTest @BeforeEach ---");
        System.out.println("TEST - @MockBean IUserService hash: " + System.identityHashCode(userService));
        System.out.println("TEST - @SpyBean DefaultDataInitializer hash: " + System.identityHashCode(defaultDataInitializer));
        System.out.println("TEST --- End of @BeforeEach ---");

        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(null);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(null);
        when(roleRepository.findByRoleName(RoleName.SUPERADMIN)).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPasswordFromMock");

        doReturn(userRoleEntity).when(userService).saveRole(argThat(r -> r != null && r.getRoleNameEnum() == RoleName.USER));
        doReturn(adminRoleEntity).when(userService).saveRole(argThat(r -> r != null && r.getRoleNameEnum() == RoleName.ADMIN));
        doReturn(superAdminRoleEntity).when(userService).saveRole(argThat(r -> r != null && r.getRoleNameEnum() == RoleName.SUPERADMIN));
        doReturn(Role.builder().id(999).roleName(RoleName.USER).build())
                .when(userService).saveRole(argThat(r -> r != null && r.getRoleNameEnum() != null &&
                        r.getRoleNameEnum() != RoleName.USER &&
                        r.getRoleNameEnum() != RoleName.ADMIN &&
                        r.getRoleNameEnum() != RoleName.SUPERADMIN));
        System.out.println("TEST - Stubs for saveRole are set up.");

        // Use invocation.getArgument() inside thenAnswer for clarity on current call's arguments
        when(userService.createUser(userArgCaptorForCreate.capture(), rolesListCaptorForCreate.capture()))
                .thenAnswer(invocation -> {
                    User userArgumentFromInvocation = invocation.getArgument(0); // Argument for THIS specific call
                    List<Role> rolesListFromInvocation = invocation.getArgument(1); // Argument for THIS specific call

                    System.out.println("TEST MOCK createUser - Email from Invocation: " + userArgumentFromInvocation.getEmail() +
                            ", RolesListFromInvocation IS NULL: " + (rolesListFromInvocation == null) +
                            (rolesListFromInvocation != null ? ", Size: " + rolesListFromInvocation.size() : ""));
                    if (rolesListFromInvocation != null && !rolesListFromInvocation.isEmpty()) {
                        System.out.println("TEST MOCK createUser - First role in RolesListFromInvocation: " + rolesListFromInvocation.get(0));
                    }

                    Integer newId = userArgumentFromInvocation.getId();
                    if (newId == null) {
                        newId = new Random().nextInt(10000) + 1000;
                    }

                    User userToReturn = User.builder()
                            .id(newId)
                            .uuid(userArgumentFromInvocation.getUuid() != null ? userArgumentFromInvocation.getUuid() : UUID.randomUUID())
                            .firstName(userArgumentFromInvocation.getFirstName())
                            .lastName(userArgumentFromInvocation.getLastName())
                            .email(userArgumentFromInvocation.getEmail())
                            .password("encodedPasswordFromCreateUserMock")
                            .roles(rolesListFromInvocation != null ? new ArrayList<>(rolesListFromInvocation) : new ArrayList<>())
                            .authProvider(userArgumentFromInvocation.getAuthProvider() != null ? userArgumentFromInvocation.getAuthProvider() : AuthProvider.LOCAL)
                            .deleted(false)
                            .createdAt(userArgumentFromInvocation.getCreatedAt() != null ? userArgumentFromInvocation.getCreatedAt() : LocalDateTime.now())
                            .updatedAt(userArgumentFromInvocation.getUpdatedAt() != null ? userArgumentFromInvocation.getUpdatedAt() : LocalDateTime.now())
                            .build();

                    System.out.println("TEST MOCK createUser - Returning User: " + userToReturn.getEmail() +
                            ", Has Roles: " + (userToReturn.getRoles() != null && !userToReturn.getRoles().isEmpty()) +
                            (userToReturn.getRoles() != null && !userToReturn.getRoles().isEmpty() ? ", Roles: " + userToReturn.getRoles().stream().map(Role::getRoleNameEnum).collect(Collectors.toList()) : ""));
                    return userToReturn;
                });

        when(userService.saveUser(userArgCaptorForSave.capture())).thenAnswer(invocation -> {
            User userArgFromInvocation = invocation.getArgument(0); // Argument for THIS specific call
            Integer currentId = userArgFromInvocation.getId();
            if (currentId == null) {
                currentId = new Random().nextInt(10000) + 1000;
            }
            List<Role> rolesToSave = userArgFromInvocation.getRoles() != null ? new ArrayList<>(userArgFromInvocation.getRoles()) : new ArrayList<>();
            return userArgFromInvocation.toBuilder()
                    .id(currentId)
                    .roles(rolesToSave)
                    .updatedAt(LocalDateTime.now()).build();
        });
    }

    @Test
    void commandLineRunner_shouldCreateRoles_ifTheyDontExist() throws Exception {
        System.out.println("TEST - Running: commandLineRunner_shouldCreateRoles_ifTheyDontExist");
        defaultDataInitializer.run(new String[]{});

        verify(userService, times(3)).saveRole(roleArgCaptorForSaveRole.capture()); // Use specific captor
        List<Role> capturedRoles = roleArgCaptorForSaveRole.getAllValues();
        assertEquals(3, capturedRoles.size());
        assertTrue(capturedRoles.stream().anyMatch(r -> r.getRoleNameEnum() == RoleName.USER));
        assertTrue(capturedRoles.stream().anyMatch(r -> r.getRoleNameEnum() == RoleName.ADMIN));
        assertTrue(capturedRoles.stream().anyMatch(r -> r.getRoleNameEnum() == RoleName.SUPERADMIN));

        // Verify createUser was called 3 times, arguments are captured by class-level captors
        verify(userService, times(3)).createUser(any(User.class), any(List.class));
        System.out.println("TEST - PASSED: commandLineRunner_shouldCreateRoles_ifTheyDontExist");
    }

    @Test
    void commandLineRunner_shouldNotCreateRoles_ifTheyExist() throws Exception {
        System.out.println("TEST - Running: commandLineRunner_shouldNotCreateRoles_ifTheyExist");
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(userRoleEntity);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(adminRoleEntity);
        when(roleRepository.findByRoleName(RoleName.SUPERADMIN)).thenReturn(superAdminRoleEntity);

        defaultDataInitializer.run(new String[]{});

        verify(userService, times(0)).saveRole(any(Role.class));
        verify(userService, times(3)).createUser(any(User.class), any(List.class));
        System.out.println("TEST - PASSED: commandLineRunner_shouldNotCreateRoles_ifTheyExist");
    }

    @Test
    void commandLineRunner_shouldCreateDefaultUsers_ifNotExist() throws Exception {
        System.out.println("TEST - Running: commandLineRunner_shouldCreateDefaultUsers_ifNotExist");

        defaultDataInitializer.run(new String[]{});

        verify(userService, times(3)).saveRole(any(Role.class));
        System.out.println("TEST - saveRole verify times(3) PASSED for shouldCreateDefaultUsers.");

        // Verify createUser calls and capture arguments
        verify(userService, times(3)).createUser(any(User.class), any(List.class));
        System.out.println("TEST - createUser verify times(3) PASSED for shouldCreateDefaultUsers.");

        List<User> capturedUserInputs = userArgCaptorForCreate.getAllValues();
        List<List<Role>> capturedRoleListsForCreateUser = rolesListCaptorForCreate.getAllValues();

        assertTrue(capturedUserInputs.stream().anyMatch(u -> "user@gmail.com".equals(u.getEmail())), "user@gmail.com input should be captured");

        boolean userGmailCreatedWithUserRole = false;
        for (int i = 0; i < capturedUserInputs.size(); i++) {
            User userInput = capturedUserInputs.get(i);
            List<Role> rolesPassedToCreate = capturedRoleListsForCreateUser.get(i);

            if ("user@gmail.com".equals(userInput.getEmail())) {
                assertNotNull(rolesPassedToCreate, "Roles list passed to createUser for user@gmail.com should not be null");
                assertFalse(rolesPassedToCreate.isEmpty(), "Roles list for user@gmail.com should not be empty");
                assertTrue(rolesPassedToCreate.stream().anyMatch(r -> r.getId().equals(userRoleEntity.getId())),
                        "user@gmail.com should have been created with USER role passed to mock");
                userGmailCreatedWithUserRole = true;
            }
        }
        assertTrue(userGmailCreatedWithUserRole, "user@gmail.com was not processed with USER role in createUser mock input");
        System.out.println("TEST - PASSED: commandLineRunner_shouldCreateDefaultUsers_ifNotExist");
    }

    @Test
    void commandLineRunner_shouldReactivateAndAddRoleToAdmin_ifExistingDeletedAndMissingRole() throws Exception {
        System.out.println("TEST - Running: commandLineRunner_shouldReactivateAndAddRoleToAdmin_ifExistingDeletedAndMissingRole");
        Role specificUserRoleForAdmin = Role.builder().id(1).roleName(RoleName.USER).build();
        Role specificAdminRoleForAdmin = Role.builder().id(2).roleName(RoleName.ADMIN).build();

        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(specificUserRoleForAdmin);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(specificAdminRoleForAdmin);

        User existingAdmin = User.builder()
                .id(5)
                .email("admin@gmail.com")
                .roles(new ArrayList<>(Collections.singletonList(specificUserRoleForAdmin)))
                .deleted(true)
                .firstName("Default-admin-user").lastName("User")
                .password("someOldEncodedPassword").authProvider(AuthProvider.LOCAL)
                .uuid(UUID.randomUUID()).createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(existingAdmin));
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("superadmin@gmail.com")).thenReturn(Optional.empty());

        defaultDataInitializer.run(new String[]{});

        verify(userService, times(1)).saveRole(argThat(r -> r.getRoleNameEnum() == RoleName.SUPERADMIN));

        verify(userService, times(1)).saveUser(any(User.class));
        User savedAdmin = userArgCaptorForSave.getValue();

        assertEquals("admin@gmail.com", savedAdmin.getEmail());
        assertFalse(savedAdmin.isDeleted());
        assertNotNull(savedAdmin.getRoles(), "Roles for savedAdmin should not be null");
        assertTrue(savedAdmin.getRoles().stream().anyMatch(r -> r.getId().equals(specificAdminRoleForAdmin.getId())));
        assertTrue(savedAdmin.getRoles().stream().anyMatch(r -> r.getId().equals(specificUserRoleForAdmin.getId())));
        assertEquals(2, savedAdmin.getRoles().size());

        verify(userService, times(2)).createUser(any(User.class), any(List.class));
        System.out.println("TEST - PASSED: commandLineRunner_shouldReactivateAndAddRoleToAdmin_ifExistingDeletedAndMissingRole");
    }
}