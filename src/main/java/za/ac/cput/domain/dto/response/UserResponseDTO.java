package za.ac.cput.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * UserResponseDTO.java
 * DTO for sending User data to the client.
 * Includes user details, roles, and a fully qualified URL to their profile image.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    /**
     * The unique identifier (UUID) for the user.
     */
    private UUID uuid;
    /**
     * The user's email address, which also serves as their username.
     */
    private String email;
    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * A list of role names assigned to the user (e.g., "ROLE_USER").
     */
    private List<String> roles;
    /**
     * The fully-qualified URL pointing to the user's profile image.
     * This will be null if no image has been uploaded.
     */
    private String profileImageUrl;
}