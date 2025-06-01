package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleName(RoleName roleName);
    //get all roles


    @Override
    List<Role> findAll();
}
