package za.ac.cput.service.impl;

import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;

import java.util.List;

public interface IRoleService {
    List<Role> findAndEnsureRoles(List<String> roleNames);
    Role findByRoleName(RoleName roleName); // Potentially useful
}
