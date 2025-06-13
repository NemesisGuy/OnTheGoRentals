package za.ac.cput.domain.mapper;

import za.ac.cput.domain.dto.request.UserCreateDTO;
import za.ac.cput.domain.dto.request.UserUpdateDTO;
import za.ac.cput.domain.dto.response.UserResponseDTO;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.service.IFileStorageService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper.java
 * A stateless utility class for mapping between User domain entities and their DTOs.
 * The `toDto` methods require an IFileStorageService instance to correctly resolve image URLs.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2025-06-13
 */
public class UserMapper {

    /**
     * Converts a User entity to a UserResponseDTO.
     * This method requires an IFileStorageService instance to generate the profile image URL.
     *
     * @param user               The User entity to convert.
     * @param fileStorageService The service instance to use for URL generation. Can be null if URL generation is not needed.
     * @return A UserResponseDTO.
     */
    public static UserResponseDTO toDto(User user, IFileStorageService fileStorageService) {
        if (user == null) {
            return null;
        }

        String profileImageUrl = null;
        // The service is passed in, so we can use it here.
        // We also check if the service is null, allowing this method to be used in contexts
        // where URL generation is not necessary.
        if (fileStorageService != null && user.getProfileImageFileName() != null && !user.getProfileImageFileName().isBlank()) {
            // Construct the key (e.g., "selfies/some-image.jpg")
            String key = user.getProfileImageType() + "/" + user.getProfileImageFileName();
            // Use the provided storage service to get the correct URL (local or MinIO pre-signed)
            profileImageUrl = fileStorageService.getUrl(key).toString();
        }

        return UserResponseDTO.builder()
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImageUrl(profileImageUrl)
                .roles(user.getRoles() != null ?
                        user.getRoles().stream()
                                .map(role -> role.getRoleName()) // Convert enum to String
                                .collect(Collectors.toList()) :    // Collect to a List<String>
                        Collections.emptyList())                   // Use emptyList() for the else case
                .build();
    }

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     *
     * @param users              The list of User entities.
     * @param fileStorageService The service instance to use for URL generation for each user.
     * @return A list of UserResponseDTOs.
     */
    public static List<UserResponseDTO> toDtoList(List<User> users, IFileStorageService fileStorageService) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(user -> toDto(user, fileStorageService)) // Pass the service for each conversion
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserCreateDTO to a new User entity, ready for persistence.
     * This method does not require external services.
     *
     * @param createDto The DTO for creating a user.
     * @return A new User entity.
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
     * This method creates a payload containing ONLY the fields that were provided in the DTO.
     *
     * @param updateDto    The DTO containing the update data.
     * @param existingUser The existing User entity (unused in this implementation but good practice for context).
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
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            updatePayload.setPassword(updateDto.getPassword());
        }
        if (updateDto.getDeleted() != null) {
            updatePayload.setDeleted(updateDto.getDeleted());
        }

        return updatePayload;
    }
}