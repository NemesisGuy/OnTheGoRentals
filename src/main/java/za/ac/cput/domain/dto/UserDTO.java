package za.ac.cput.domain.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.security.Role;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
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
