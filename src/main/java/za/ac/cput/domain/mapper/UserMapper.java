package za.ac.cput.domain.mapper;

import za.ac.cput.domain.security.User; // Your User Entity
import za.ac.cput.domain.dto.response.UserResponseDTO;
// Import UserCreateDTO, UserUpdateDTO if you have them and need toEntity methods for them

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

public class UserMapper {

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param user The User entity.
     * @return The corresponding UserResponseDTO, or null if the user entity is null.
     */
    public static UserResponseDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        List<String> roleNames = Collections.emptyList();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roleNames = user.getRoles().stream()
                    .map(role -> role.getRoleName()) // Assuming Role has getRoleName() returning an enum
                    .collect(Collectors.toList());
        }

        return UserResponseDTO.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roleNames)
                .build();
    }

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users List of User entities.
     * @return List of UserResponseDTOs.
     */
    public static List<UserResponseDTO> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    // Add toEntity methods if you need to map UserCreateDTO or UserUpdateDTO to User entity
    // Example for a hypothetical UserCreateDTO:
    /*
    public static User toEntity(UserCreateDTO createDto) {
        if (createDto == null) return null;
        User user = new User();
        // UUID will be set by @PrePersist
        user.setEmail(createDto.getEmail());
        user.setFirstName(createDto.getFirstName());
        user.setLastName(createDto.getLastName());
        // Password would be handled and encoded in the service layer
        // Roles would be fetched and assigned in the service layer
        return user;
    }
    */

    // Example for updating an existing user from a hypothetical UserUpdateDTO:
    /*
    public static void updateEntityFromDto(UserUpdateDTO updateDto, User existingUser) {
        if (updateDto == null || existingUser == null) return;

        if (updateDto.getFirstName() != null) {
            existingUser.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            existingUser.setLastName(updateDto.getLastName());
        }
        if (updateDto.getEmail() != null) {
            existingUser.setEmail(updateDto.getEmail()); // Handle email uniqueness in service
        }
        // Password & roles updates are typically handled more explicitly in the service layer
    }
    */
}