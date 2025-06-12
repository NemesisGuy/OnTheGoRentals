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
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2025-06-12
 */
@Component
public class UserMapper {

    // ... toDto and toDtoList methods are correct and unchanged ...
    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;

        List<String> roleNames = user.getRoles() != null ?
                user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()) :
                Collections.emptyList();

        UserResponseDTO.UserResponseDTOBuilder dtoBuilder = UserResponseDTO.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roleNames);

        if (user.getProfileImageFileName() != null && !user.getProfileImageFileName().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(user.getProfileImageType() + "/")
                    .path(user.getProfileImageFileName())
                    .toUriString();
            dtoBuilder.profileImageUrl(imageUrl);
        }
        return dtoBuilder.build();
    }

    public static List<UserResponseDTO> toDtoList(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    // ... toEntity from UserCreateDTO is correct and unchanged ...
    public static User toEntity(UserCreateDTO createDto) {
        if (createDto == null) return null;
        return User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
                .password(createDto.getPassword())
                .authProvider(createDto.getAuthProvider())
                .googleId(createDto.getGoogleId())
                .build();
    }

    /**
     * **THE DEFINITIVE FIX**
     * Applies updates from a UserUpdateDTO to a new, "sparse" User object.
     * This method creates a payload containing ONLY the fields that were provided in the DTO.
     * This prevents old data (like a hashed password) from being accidentally passed
     * to the update service.
     *
     * @param updateDto The DTO containing the update data.
     * @param existingUser The existing User entity (not used in this new implementation, but kept for signature consistency).
     * @return A new User instance containing only the fields to be updated.
     */
    public static User applyUpdateDtoToEntity(UserUpdateDTO updateDto, User existingUser) {
        if (updateDto == null) {
            throw new IllegalArgumentException("Update DTO must not be null.");
        }

        // Create a new, empty User object to act as a "sparse" payload.
        User updatePayload = new User();

        // Only set fields on the payload if they exist in the DTO.
        if (updateDto.getFirstName() != null) {
            updatePayload.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            updatePayload.setLastName(updateDto.getLastName());
        }
        if (updateDto.getEmail() != null) {
            updatePayload.setEmail(updateDto.getEmail());
        }
        // Only set the password on the payload if a NEW one is provided in the DTO.
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            updatePayload.setPassword(updateDto.getPassword());
        }
        if (updateDto.getDeleted() != null) {
            updatePayload.setDeleted(updateDto.getDeleted());
        }

        // Return the sparse payload. All other fields on this object will be null.
        return updatePayload;
    }
}