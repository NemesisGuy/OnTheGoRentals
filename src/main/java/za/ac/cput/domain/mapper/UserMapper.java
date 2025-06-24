package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.UserCreateDTO;
import za.ac.cput.domain.dto.request.UserUpdateDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.BadRequestException;
import za.ac.cput.service.IFileStorageService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper.java
 * A stateless utility class for mapping between User domain entities and their DTOs.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.1
 */
public class UserMapper {

    /**
     * Converts a User entity to a UserResponseDTO.
     * This method requires the public API URL to correctly resolve image URLs.
     *
     * @param user               The User entity to convert.
     * @param fileStorageService The storage service (can be null if not needed).
     * @param publicApiUrl       The base public URL of the API.
     * @return A UserResponseDTO.
     */
    public static UserResponseDTO toDto(User user, IFileStorageService fileStorageService, String publicApiUrl) {
        if (user == null) {
            return null;
        }

        String profileImageUrl = null;
        if (publicApiUrl != null && user.getProfileImageFileName() != null && !user.getProfileImageFileName().isBlank()) {
            String key = user.getProfileImageType() + "/" + user.getProfileImageFileName();
            profileImageUrl = publicApiUrl + "/api/v1/files/" + key;
        }

        return UserResponseDTO.builder()
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImageUrl(profileImageUrl)
                .roles(user.getRoles() != null ?
                        // THE FIX IS HERE: Correctly get the string name of the enum
                        user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users              The list of User entities.
     * @param fileStorageService The storage service.
     * @param publicApiUrl       The base public URL of the API.
     * @return A list of UserResponseDTOs.
     */
    public static List<UserResponseDTO> toDtoList(List<User> users, IFileStorageService fileStorageService, String publicApiUrl) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(user -> toDto(user, fileStorageService, publicApiUrl))
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserCreateDTO to a new User entity.
     */
    public static User toEntity(UserCreateDTO createDto) {
        if (createDto == null) return null;
        return User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
                .password(createDto.getPassword())
                .build();
    }

    /**
     * Applies updates from a UserUpdateDTO to a new, "sparse" User object.
     */
    public static User applyUpdateDtoToEntity(UserUpdateDTO updateDto, User existingUser) {
        if (updateDto == null) {
            throw new IllegalArgumentException("Update DTO must not be null.");
        }
        User updatePayload = new User();
        if (updateDto.getFirstName() != null) updatePayload.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) updatePayload.setLastName(updateDto.getLastName());
        if (updateDto.getEmail() != null) updatePayload.setEmail(updateDto.getEmail());
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            updatePayload.setPassword(updateDto.getPassword());
        }
        if (updateDto.getDeleted() != null) updatePayload.setDeleted(updateDto.getDeleted());
        if (updateDto.getRoleNames() != null) {
            List<Role> pseudoRoles = updateDto.getRoleNames().stream()
                    .map(roleNameString -> {
                        try {
                            RoleName roleEnum = RoleName.valueOf(roleNameString.trim().toUpperCase());
                            Role tempRole = new Role();
                            tempRole.setRoleName(roleEnum);
                            return tempRole;
                        } catch (IllegalArgumentException e) {
                            throw new BadRequestException("Invalid role name provided: '" + roleNameString + "'");
                        }
                    })
                    .collect(Collectors.toList());
            updatePayload.setRoles(pseudoRoles);
        }
        return updatePayload;
    }
}