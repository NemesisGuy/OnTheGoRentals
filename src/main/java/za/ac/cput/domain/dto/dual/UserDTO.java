package za.ac.cput.domain.dto.dual;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.security.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID uuid;
//    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Role> roles;

@JsonGetter("roles")
public List<String> getRoles() {
    if (roles == null) return new ArrayList<>();
    return roles.stream().map(Role::getRoleName).toList();
}
    public List<Role> getRolesList() {
        return roles;
    }

}
