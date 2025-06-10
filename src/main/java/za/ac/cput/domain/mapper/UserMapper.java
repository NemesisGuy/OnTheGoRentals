package za.ac.cput.domain.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import za.ac.cput.domain.dto.request.UserCreateDTO;
import za.ac.cput.domain.dto.request.UserUpdateDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.security.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper.java
 * A utility class for mapping between User domain entities and their DTOs.
 * Handles the construction of the profile image URL for response DTOs.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Component
public class UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserResponseDTO}.
     * It constructs the full profile image URL if an image has been uploaded.
     *
     * @param user The User entity to convert.
     * @return The resulting UserResponseDTO.
     */
    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;

        List<String> roleNames = user.getRoles() != null ?
                user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()) :
                Collections.emptyList();

        UserResponseDTO dtoBuilder = UserResponseDTO.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roleNames)
                .build();

        // Build the profile image URL if the data exists
        if (user.getProfileImageFileName() != null && !user.getProfileImageFileName().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(user.getProfileImageType() + "/")
                    .path(user.getProfileImageFileName())
                    .toUriString();
            dtoBuilder.setProfileImageUrl(imageUrl);
        }

        return dtoBuilder;
    }

    /**
     * Converts a list of {@link User} entities to a list of {@link UserResponseDTO}s.
     *
     * @param users The list of User entities.
     * @return A list of UserResponseDTOs.
     */
    public static List<UserResponseDTO> toDtoList(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Converts a {@link UserCreateDTO} to a {@link User} entity.
     * The service layer is responsible for encoding the plain text password.
     *
     * @param createDto The DTO with user creation data.
     * @return A new User entity.
     */
    public static User toEntity(UserCreateDTO createDto) {
        if (createDto == null) return null;
        return User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
                .password(createDto.getPassword()) // Pass plain password; service will encode
                .authProvider(createDto.getAuthProvider())
                .googleId(createDto.getGoogleId())
                .build();
    }

    /**
     * Applies updates from a {@link UserUpdateDTO} to an existing {@link User} entity.
     *
     * @param updateDto    The DTO with update data.
     * @param existingUser The existing User entity.
     * @return The User entity with updated fields.
     */
    public static User applyUpdateDtoToEntity(UserUpdateDTO updateDto, User existingUser) {
        if (updateDto == null || existingUser == null) {
            throw new IllegalArgumentException("Update DTO and existing User entity must not be null.");
        }

        // Use the 'toBuilder' pattern to create a mutable copy
        User.UserBuilder builder = existingUser.toBuilder();

        if (updateDto.getFirstName() != null) builder.firstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) builder.lastName(updateDto.getLastName());
        if (updateDto.getEmail() != null) builder.email(updateDto.getEmail());
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            builder.password(updateDto.getPassword()); // Service must handle encoding
        }
        if (updateDto.getDeleted() != null) builder.deleted(updateDto.getDeleted());

        // Roles are handled by the service. Image is handled by a separate endpoint.
        return builder.build();
    }
}