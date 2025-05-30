package za.ac.cput.domain.mapper;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.dto.request.UserCreateDTO; // Using generic create DTO
import za.ac.cput.domain.dto.request.UserUpdateDTO; // Using generic update DTO
import za.ac.cput.domain.dto.response.UserResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class UserMapper {

    public static UserResponseDTO toDto(User user) {
        if (user == null) return null;
        List<String> roleNames = (user.getRoles() != null) ?
                user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()) :
                Collections.emptyList();
        return UserResponseDTO.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roleNames)
                .build();
    }

    public static List<UserResponseDTO> toDtoList(List<User> users) {
        if (users == null) return null;
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    // For Admin Creating a User, using UserCreateDTO
    public static User toEntity(UserCreateDTO createDto) {
        if (createDto == null) return null;
        User.UserBuilder builder =  User.builder()
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .email(createDto.getEmail())
                .password(createDto.getPassword()); // Pass plain password; service will encode

        if (createDto.getAuthProvider() != null) {
            builder.authProvider(createDto.getAuthProvider());
        }
        if (createDto.getGoogleId() != null) {
            builder.googleId(createDto.getGoogleId());
        }
        if (createDto.getProfileImageUrl() != null) {
            builder.profileImageUrl(createDto.getProfileImageUrl());
        }
        // Roles are set here
        // UUID is set by @PrePersist.  Deleted defaults to false.
        return builder.build();
    }

    // For Admin Updating a User, using UserUpdateDTO
    public static User applyUpdateDtoToEntity(UserUpdateDTO updateDto, User existingUser) {
        if (updateDto == null || existingUser == null) {
            throw new IllegalArgumentException("Update DTO and existing User entity must not be null.");
        }

        User.UserBuilder builder =  User.builder()
                .id(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .email(existingUser.getEmail())
                .password(existingUser.getPassword());// Keep existing password unless updated




        if (updateDto.getFirstName() != null) builder.firstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) builder.lastName(updateDto.getLastName());
        if (updateDto.getEmail() != null) builder.email(updateDto.getEmail());

        // Password from DTO is plain text for update, service will encode it if it's a new password.
        // The builder will just take the value; service needs to compare with old hashed password.
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            builder.password(updateDto.getPassword());
        }

        if (updateDto.getAuthProvider() != null) builder.authProvider(updateDto.getAuthProvider());
        if (updateDto.getGoogleId() != null) builder.googleId(updateDto.getGoogleId());
        if (updateDto.getProfileImageUrl() != null) builder.profileImageUrl(updateDto.getProfileImageUrl());
        if (updateDto.getDeleted() != null) builder.deleted(updateDto.getDeleted());

        // Roles are handled by the service based on updateDto.getRoleNames()
        // AccountLocked, Enabled flags would also be applied here if present in DTO & builder
        return builder.build();
    }
}