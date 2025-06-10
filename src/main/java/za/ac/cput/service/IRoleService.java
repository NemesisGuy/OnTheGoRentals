package za.ac.cput.service;

import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;

import java.util.List;

/**
 * IRoleService.java
 * Interface defining the contract for role-related services.
 * This includes finding roles by name and resolving lists of role names
 * into managed {@link Role} entities.
 * <p>
 * Author: Peter Buckingham // Assuming based on context, please confirm/correct
 * Date: 2025-05-28 // Assuming created around this refactoring
 */
public interface IRoleService {

    /**
     * Finds a {@link Role} entity by its {@link RoleName} enum.
     *
     * @param roleName The {@link RoleName} enum to search for. Cannot be null.
     * @return The found {@link Role} entity.
     * @throws IllegalArgumentException                       if roleName is null.
     * @throws za.ac.cput.exception.ResourceNotFoundException if the role corresponding to roleName is not found in the database.
     */
    Role findByRoleName(RoleName roleName);

    /**
     * Resolves a list of role name strings into a list of managed {@link Role} entities.
     * Invalid role names or names not found in the database are logged and skipped.
     * If the input list is null, empty, or all names are invalid/not found, an empty list is returned.
     * The caller (e.g., UserService during user creation) might then apply a default role.
     *
     * @param roleNameStrings A list of strings representing role names.
     * @return A list of resolved {@link Role} entities. Can be empty.
     */
    List<Role> resolveRoles(List<String> roleNameStrings);

    /**
     * Extracts a list of role name strings from a list of {@link Role} entities.
     *
     * @param roles A list of {@link Role} entities.
     * @return A list of role name strings. Returns an empty list if the input is null or empty.
     */
    List<String> getRoleNames(List<Role> roles);
}