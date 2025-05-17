package za.ac.cput.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value; // For jwtExpiration
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.dto.*;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.RoleName;
import za.ac.cput.domain.security.User;
import za.ac.cput.domain.security.RefreshToken; // Import
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IRefreshTokenService; // Import
import za.ac.cput.service.IUserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
// @RequiredArgsConstructor // If using this, ensure IRefreshTokenService is in the constructor
public class UserService implements IUserService {

    private final AuthenticationManager authenticationManager;
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final IRefreshTokenService refreshTokenService; // Autowire this

    @Value("${jwt.expiration}") // Access token expiration from JwtUtilities
    private Long accessTokenExpirationMs;

    // Constructor injection
    public UserService(AuthenticationManager authenticationManager, IUserRepository iUserRepository,
                       IRoleRepository iRoleRepository, PasswordEncoder passwordEncoder,
                       JwtUtilities jwtUtilities, IRefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.iUserRepository = iUserRepository;
        this.iRoleRepository = iRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilities = jwtUtilities;
        this.refreshTokenService = refreshTokenService;
    }


    @Override
    public Role saveRole(Role role) {
        return iRoleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return iUserRepository.save(user);
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.SEE_OTHER);
        } else {
            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            Role role = iRoleRepository.findByRoleName(RoleName.USER);
            user.setRoles(Collections.singletonList(role));
            User savedUser = iUserRepository.save(user);

            String accessToken = jwtUtilities.generateToken(savedUser.getEmail(), Collections.singletonList(role.getRoleName()));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

            return ResponseEntity.ok(new AuthResponseDto(
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer",
                    accessTokenExpirationMs, // Send access token expiry
                    savedUser.getEmail(),
                    Collections.singletonList(role.getRoleName())
            ));
        }
    }

    // Modify authenticate to return AuthResponseDto
    public AuthResponseDto authenticateAndGenerateTokens(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = iUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authentication.getName()));

        List<String> rolesNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        String accessToken = jwtUtilities.generateToken(user.getUsername(), rolesNames);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponseDto(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                accessTokenExpirationMs,
                user.getUsername(),
                rolesNames
        );
    }

    // Keep your existing authenticate method signature if used elsewhere, or refactor
    @Override
    public String authenticate(LoginDto loginDto) {
        // This method now primarily serves to get the access token for simplicity if other parts rely on it.
        // For a full auth response, call authenticateAndGenerateTokens
        AuthResponseDto authResponse = authenticateAndGenerateTokens(loginDto);
        return authResponse.getAccessToken(); // Or consider changing IUserService interface
    }


    public TokenRefreshResponseDto refreshToken(String requestRefreshToken) {
        // Validate the refresh token
        System.out.println("Requesting refresh token: " + requestRefreshToken);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyDeviceAndExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // (Important for security) Refresh Token Rotation:
                    // Delete the old refresh token
                    refreshTokenService.deleteByToken(requestRefreshToken);
                    // Create a new one
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

                    List<String> rolesNames = user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    String newAccessToken = jwtUtilities.generateToken(user.getEmail(), rolesNames);

                    return new TokenRefreshResponseDto(
                            newAccessToken,
                            newRefreshToken.getToken(), // Send the NEW refresh token
                            "Bearer",
                            accessTokenExpirationMs
                    );
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    // Add logout functionality if needed
    public void logoutUser(Integer userId) {
        // This invalidates the refresh token, forcing re-login
        refreshTokenService.deleteByUserId(userId);
        // SecurityContextHolder.clearContext(); // Clear context if session based, but JWT is stateless server-side.
        // Client should discard tokens.
    }


    // ... rest of your UserService methods (getAll, create, read, update, delete, etc.)
    // They remain largely unchanged by refresh token logic.
    public List<User> getAll() {
        List<User> users = iUserRepository.findAll();
        return users;
    }

    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return iUserRepository.save(user);
    }

    public User read(Integer id) {
        return iUserRepository.findById(id).orElse(null);
    }


    @Override
    public User update(Integer id, User user) {
        if (iUserRepository.existsById(id)) {
            User existingUser = iUserRepository.findById(id).orElse(null);
            if (existingUser != null) {
                user.setId(id);

                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    // Keep the existing password if none provided
                    user.setPassword(existingUser.getPassword());
                } else {
                    // Encode only if password is not already encoded
                    if (passwordNeedsEncoding(user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                }
                return iUserRepository.save(user);
            }
        }
        return null;
    }


    public User update(User user) { // Overloaded, ensure this is intended
        return update(user.getId(), user);
    }

    public boolean delete(Integer id) {
        // Before deleting user, consider deleting associated refresh tokens
        // refreshTokenService.deleteByUserId(id); // Or handle via cascade delete if configured
        //Soft delete
        User user = iUserRepository.findById(id).orElse(null);
        if (user == null) {
            return false;
        }else {
            user.setDeleted(true);
            iUserRepository.save(user);
            return true;
        }

  /*      iUserRepository.deleteById(id);
        return !iUserRepository.existsById(id);*/

    }
    public User read(String email) {
        User user = iUserRepository.findByEmail(email).orElse(null);
        if (user != null) {
            User userCopy = new User();
            userCopy.setId(user.getId());
            userCopy.setEmail(user.getEmail());
            userCopy.setFirstName(user.getFirstName());
            userCopy.setLastName(user.getLastName());
            userCopy.setRoles(user.getRoles());
            userCopy.setPassword(null);
            return userCopy;
        }
        return null;
    }
    public UserDTO readDTO(int id) {
        User user = read(id);
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles());
    }
    public UserDTO readDTO(String email) {
        User user = read(email);
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles());
    }

    private boolean passwordNeedsEncoding(String password) {
        // This is a simplistic check — adjust depending on your encoder
        return password != null && !password.startsWith("$2a$"); // bcrypt hashes start with $2a$, for example
    }
}