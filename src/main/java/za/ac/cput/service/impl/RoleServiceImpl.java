package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.exception.ResourceNotFoundException; // For role not found
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.service.IRoleService;

import java.util.ArrayList;
import java.util.Collections; // For List.of()
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoleServiceImpl.java
 * Implementation of the {@link IRoleService} interface.
 * Provides services for finding roles and resolving role names to {@link Role} entities.
 *
 * Author: Peter Buckingham // Assuming based on context, please confirm/correct
 * Date: 2025-05-28 // Assuming created around this refactoring
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
public class RoleServiceImpl implements IRoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final IRoleRepository roleRepository;

    /**
     * Constructs the RoleServiceImpl with the necessary role repository.
     *
     * @param roleRepository The repository for role persistence and retrieval.
     */
    @Autowired
    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        log.info("RoleServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role findByRoleName(RoleName roleName) {
        log.debug("Attempting to find role by RoleName enum: {}", roleName);
        if (roleName == null) {
            log.error("RoleName cannot be null when finding a role.");
            throw new IllegalArgumentException("Role name enum cannot be null.");
        }

        Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName.name())); // Use roleName.name() if repository expects String
        // Or just roleName if repository expects RoleName enum
        if (role == null) {
            log.warn("Role not found in database for RoleName: {}", roleName);
            throw new ResourceNotFoundException("Role not found: " + roleName);
        }
        log.debug("Role found: ID: {}, Name: '{}'", role.getId(), role.getRoleName());
        return role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true) // This method only reads, so readOnly transaction is appropriate
    public List<Role> resolveRoles(List<String> roleNameStrings) {
        log.debug("Attempting to resolve role name strings to Role entities. Input role names: {}", roleNameStrings);
        if (roleNameStrings == null || roleNameStrings.isEmpty()) {
            log.debug("Input role name list is null or empty. Returning empty list of roles.");
            return Collections.emptyList();
        }

        List<Role> resolvedRoles = new ArrayList<>();
        for (String roleNameStr : roleNameStrings) {
            if (roleNameStr == null || roleNameStr.trim().isEmpty()) {
                log.warn("Encountered null or empty role name string. Skipping.");
                continue;
            }
            try {
                // Assuming RoleName enum stores values like "USER", "ADMIN", etc.
                // And IRoleRepository.findByRoleName(String roleName) exists
                String processedRoleNameStr = roleNameStr.trim().toUpperCase();
                RoleName roleEnum = RoleName.valueOf(processedRoleNameStr); // Validate against enum
                Role role = roleRepository.findByRoleName(RoleName.valueOf(roleEnum.name())); // Find in DB by string name from enum

                if (role == null) {
                    log.warn("Role '{}' (from string '{}') not found in database. Skipping.", roleEnum.name(), roleNameStr);
                    // Depending on requirements, you might throw an exception here if all roles must exist.
                    // Or, if creating missing roles is desired (and allowed), that logic would go here.
                } else {
                    resolvedRoles.add(role);
                    log.debug("Successfully resolved and added role: '{}'", role.getRoleName());
                }
            } catch (IllegalArgumentException e) {
                // This catches if RoleName.valueOf() fails for an invalid string
                log.warn("Invalid role name string '{}' encountered. It does not match any defined RoleName enum constant. Skipping.", roleNameStr, e);
            }
        }

        if (resolvedRoles.isEmpty() && !roleNameStrings.isEmpty()) {
            log.warn("No valid roles were resolved from the provided list: {}. An empty role list will be returned.", roleNameStrings);
        } else {
            log.debug("Successfully resolved {} roles from input list.", resolvedRoles.size());
        }
        // It's the responsibility of the calling service (e.g., UserService)
        // to handle the case where resolvedRoles is empty (e.g., by assigning a default role).
        return resolvedRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRoleNames(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            log.debug("Input list of Role entities is null or empty. Returning empty list of role names.");
            return Collections.emptyList();
        }
        List<String> roleNames = roles.stream()
                .map(Role::getRoleName) // Assuming Role.getRoleName() returns String
                .collect(Collectors.toList());
        log.debug("Extracted role names from Role entities: {}", roleNames);
        return roleNames;
    }

    // The commented-out findAndEnsureRoles method seems to have similar intent to resolveRoles.
    // If it had different logic (e.g., creating roles if not found), that would be a distinct feature.
    // For now, resolveRoles handles the described behavior of finding existing roles.
}