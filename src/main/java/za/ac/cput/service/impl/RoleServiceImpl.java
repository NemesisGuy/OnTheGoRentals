package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For ensureRoles
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.repository.IRoleRepository;


import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {
    private final IRoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional // If creating roles, it should be transactional
    public List<Role> findAndEnsureRoles(List<String> roleNameStrings) {
        List<Role> roles = new ArrayList<>();
        if (roleNameStrings == null || roleNameStrings.isEmpty()) {
            // Default role logic if needed, or let User's @PrePersist handle it
            // For now, we return an empty list if DTO provides empty/null.
            // The User entity's @PrePersist will add the default "USER" role in this case.
            return roles;
        }

        for (String roleNameStr : roleNameStrings) {
            try {
                RoleName roleEnum = RoleName.valueOf(roleNameStr.trim().toUpperCase());
                Role role = roleRepository.findByRoleName(roleEnum);
                roles.add(role);
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Invalid role name string '" + roleNameStr + "' encountered in RoleService.");
                // Optionally, throw a specific application exception to be caught by controller
            }
        }
        return roles;
    }

    @Override
    public Role findByRoleName(RoleName roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        // Use the repository to find the role by its name
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName); // Or custom exception
        }
        return role;
    }
}