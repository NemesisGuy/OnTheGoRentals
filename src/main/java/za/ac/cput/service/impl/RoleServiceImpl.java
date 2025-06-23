package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.service.IRoleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link IRoleService} interface.
 * Provides services for finding roles and resolving role names to {@link Role} entities.
 * This service acts as a bridge between string-based role representations (e.g., from DTOs)
 * and the persisted {@code Role} entities used within the application's security context.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-28
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Service
public class RoleServiceImpl implements IRoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final IRoleRepository roleRepository;

    /**
     * Constructs the RoleServiceImpl with the required repository dependency.
     *
     * @param roleRepository The repository for accessing {@link Role} data, injected by Spring.
     */
    @Autowired
    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        log.info("RoleServiceImpl initialized.");
    }

    /**
     * Finds a single {@link Role} entity by its corresponding {@link RoleName} enum.
     *
     * @param roleNameEnum The enum constant representing the desired role (e.g., RoleName.USER).
     * @return The persisted {@link Role} entity.
     * @throws IllegalArgumentException if the provided {@code roleNameEnum} is null.
     * @throws ResourceNotFoundException if no role with the given name exists in the database.
     */
    @Override
    public Role findByRoleName(RoleName roleNameEnum) {
        log.debug("Attempting to find role by RoleName enum: {}", roleNameEnum);
        if (roleNameEnum == null) {
            log.error("RoleName enum cannot be null when finding a role.");
            throw new IllegalArgumentException("Role name enum cannot be null.");
        }

        Role role = roleRepository.findByRoleName(roleNameEnum);

        if (role == null) {
            log.warn("Role not found in database for RoleName: {}", roleNameEnum);
            throw new ResourceNotFoundException("Role not found: " + roleNameEnum);
        }
        log.debug("Role found: ID: {}, Name: '{}'", role.getId(), role.getRoleName());
        return role;
    }

    /**
     * Resolves a list of role name strings into a list of managed {@link Role} entities.
     * This method is marked as read-only for performance optimization. It safely handles invalid
     * or non-existent role names by logging a warning and skipping them.
     *
     * @param roleNameStrings A list of strings representing the role names to resolve (e.g., ["USER", "ADMIN"]).
     * @return A {@link List} of the corresponding persisted {@link Role} entities. Returns an empty list
     *         if the input is null, empty, or contains no valid/existing role names.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> resolveRoles(List<String> roleNameStrings) {
        log.debug("Attempting to resolve role name strings to Role entities. Input role names: {}", roleNameStrings);
        if (roleNameStrings == null || roleNameStrings.isEmpty()) {
            log.debug("Input role name list is null or empty. Returning empty list of roles.");
            return Collections.emptyList();
        }

        List<Role> resolvedRoles = new ArrayList<>();
        for (String roleNameStr : roleNameStrings) {
            if (roleNameStr == null || roleNameStr.trim().isEmpty()) {
                log.warn("Encountered null or empty role name string in list. Skipping.");
                continue;
            }
            try {
                String processedRoleNameStr = roleNameStr.trim().toUpperCase();
                RoleName roleEnum = RoleName.valueOf(processedRoleNameStr);

                Role role = roleRepository.findByRoleName(roleEnum);

                if (role == null) {
                    log.warn("Role '{}' (from string '{}') not found in database. Skipping.", roleEnum.name(), roleNameStr);
                } else {
                    resolvedRoles.add(role);
                    log.debug("Successfully resolved and added role: '{}' (ID: {})",
                            role.getRoleName(), role.getId());
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role name string '{}' encountered. It does not match any defined RoleName enum constant. Skipping.", roleNameStr, e);
            }
        }

        if (resolvedRoles.isEmpty() && !roleNameStrings.isEmpty()) {
            log.warn("No valid or existing roles were resolved from the provided list: {}. An empty role list will be returned.", roleNameStrings);
        } else {
            log.debug("Successfully resolved {} roles from input list of {} strings.", resolvedRoles.size(), roleNameStrings.size());
        }
        return resolvedRoles;
    }

    /**
     * Converts a list of {@link Role} entities back into a list of their string names.
     * This is the inverse operation of {@link #resolveRoles(List)}.
     *
     * @param roles A list of {@link Role} entities.
     * @return A {@link List} of string representations of the role names. Returns an empty list
     *         if the input is null or empty.
     */
    @Override
    public List<String> getRoleNames(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            log.debug("Input list of Role entities is null or empty. Returning empty list of role names.");
            return Collections.emptyList();
        }
        List<String> roleNameStrings = roles.stream()
                .map(Role::getRoleName)   // This correctly returns the RoleName enum instance
                .collect(Collectors.toList());
        log.debug("Extracted role names from Role entities: {}", roleNameStrings);
        return roleNameStrings;
    }
}