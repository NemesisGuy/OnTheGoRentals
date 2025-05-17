// File: UserMapper.java
// Location: za.ac.cput.domain.mapper

package za.ac.cput.domain.mapper;


import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.security.User;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles()) // List<String> assumed
                .build();
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRoles(dto.getRolesList()); // List<String> assumed
        return user;
    }
}
