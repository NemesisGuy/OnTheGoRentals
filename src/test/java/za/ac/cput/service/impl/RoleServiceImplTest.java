package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IRoleRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RoleServiceImpl}.
 * Assumes IRoleRepository.findByRoleName(RoleName) returns a Role object or null.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-30
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role roleUser;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        roleUser = new Role(RoleName.USER);
        roleUser.setId(1);

        roleAdmin = new Role(RoleName.ADMIN);
        roleAdmin.setId(2);
    }

    // --- findByRoleName Tests ---
    @Test
    void findByRoleName_shouldReturnRole_whenRoleNameEnumExists() {
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);

        Role foundRole = roleService.findByRoleName(RoleName.USER);

        assertNotNull(foundRole);
        assertEquals(RoleName.USER, foundRole.getRoleNameEnum());
        assertEquals(roleUser.getId(), foundRole.getId());
        verify(roleRepository, times(1)).findByRoleName(RoleName.USER);
    }

    @Test
    void findByRoleName_shouldThrowIllegalArgumentException_whenRoleNameEnumIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.findByRoleName(null);
        });
        assertEquals("Role name enum cannot be null.", exception.getMessage());
        verify(roleRepository, never()).findByRoleName(any(RoleName.class));
    }

    @Test
    void findByRoleName_shouldThrowResourceNotFoundException_whenRoleNotInDatabase() {
        when(roleRepository.findByRoleName(RoleName.SUPERADMIN)).thenReturn(null); // Return null if not found

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.findByRoleName(RoleName.SUPERADMIN);
        });
        assertEquals("Role not found: SUPERADMIN", exception.getMessage());
    }

    // --- resolveRoles Tests ---
    @Test
    void resolveRoles_shouldReturnListOfRoles_whenNamesAreValidAndExist() {
        List<String> roleNameStrings = List.of("USER", "ADMIN");
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(roleAdmin);

        List<Role> resolvedRoles = roleService.resolveRoles(roleNameStrings);

        assertNotNull(resolvedRoles);
        assertEquals(2, resolvedRoles.size());
        assertTrue(resolvedRoles.stream().anyMatch(r -> r.getRoleNameEnum() == RoleName.USER));
        assertTrue(resolvedRoles.stream().anyMatch(r -> r.getRoleNameEnum() == RoleName.ADMIN));
        verify(roleRepository, times(1)).findByRoleName(RoleName.USER);
        verify(roleRepository, times(1)).findByRoleName(RoleName.ADMIN);
    }

    @Test
    void resolveRoles_shouldReturnEmptyList_whenInputListIsNull() {
        List<Role> resolvedRoles = roleService.resolveRoles(null);
        assertNotNull(resolvedRoles);
        assertTrue(resolvedRoles.isEmpty());
    }

    @Test
    void resolveRoles_shouldReturnEmptyList_whenInputListIsEmpty() {
        List<Role> resolvedRoles = roleService.resolveRoles(Collections.emptyList());
        assertNotNull(resolvedRoles);
        assertTrue(resolvedRoles.isEmpty());
    }

    @Test
    void resolveRoles_shouldSkipInvalidRoleNameStrings() {
        List<String> roleNameStrings = List.of("USER", "INVALID_ROLE", "   admin  ");
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(roleAdmin);

        List<Role> resolvedRoles = roleService.resolveRoles(roleNameStrings);

        assertNotNull(resolvedRoles);
        assertEquals(2, resolvedRoles.size());
    }

    @Test
    void resolveRoles_shouldSkipRoleNamesNotInDatabase() {
        List<String> roleNameStrings = List.of("USER", "SUPERADMIN");
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);
        when(roleRepository.findByRoleName(RoleName.SUPERADMIN)).thenReturn(null); // SUPERADMIN role not found

        List<Role> resolvedRoles = roleService.resolveRoles(roleNameStrings);

        assertNotNull(resolvedRoles);
        assertEquals(1, resolvedRoles.size());
        assertEquals(RoleName.USER, resolvedRoles.get(0).getRoleNameEnum());
    }

    @Test
    void resolveRoles_shouldHandleNullAndEmptyStringsInListGracefully() {
        List<String> roleNameStrings = new ArrayList<>();
        roleNameStrings.add("USER");
        roleNameStrings.add(null);
        roleNameStrings.add("");
        roleNameStrings.add("  ");
        roleNameStrings.add("ADMIN");

        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(roleAdmin);

        List<Role> resolvedRoles = roleService.resolveRoles(roleNameStrings);

        assertNotNull(resolvedRoles);
        assertEquals(2, resolvedRoles.size());
    }

    // --- getRoleNames Tests (remain the same) ---
    @Test
    void getRoleNames_shouldReturnListOfStrings_whenGivenListOfRoles() {
        List<Role> roles = List.of(roleUser, roleAdmin);
        List<String> roleNameStrings = roleService.getRoleNames(roles);
        assertNotNull(roleNameStrings);
        assertEquals(2, roleNameStrings.size());
        assertTrue(roleNameStrings.contains(RoleName.USER.name()));
        assertTrue(roleNameStrings.contains(RoleName.ADMIN.name()));
    }

    @Test
    void getRoleNames_shouldReturnEmptyList_whenInputRoleListIsNull() {
        List<String> roleNameStrings = roleService.getRoleNames(null);
        assertNotNull(roleNameStrings);
        assertTrue(roleNameStrings.isEmpty());
    }

    @Test
    void getRoleNames_shouldReturnEmptyList_whenInputRoleListIsEmpty() {
        List<String> roleNameStrings = roleService.getRoleNames(Collections.emptyList());
        assertNotNull(roleNameStrings);
        assertTrue(roleNameStrings.isEmpty());
    }
}