package za.ac.cput.service.impl;

import jakarta.servlet.http.HttpServletResponse; // For setting cookies
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;     // For setting cookies in headers
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;  // Spring's cookie builder
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.dto.response.TokenRefreshResponseDto;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.RoleName;
import za.ac.cput.domain.security.User;
import za.ac.cput.domain.security.RefreshToken;
import za.ac.cput.exception.TokenRefreshException;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IRefreshTokenService;
import za.ac.cput.service.IUserService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final IRefreshTokenService refreshTokenService;

    @Value("${jwt.expiration}")
    private Long accessTokenExpirationMs;

    @Value("${jwt.refresh-token.expiration-ms}") // From application.properties
    private Long refreshTokenDurationMs;

    @Value("${app.security.refresh-cookie.name}") // New property for cookie name
    private String refreshTokenCookieName;

    @Value("${app.security.refresh-cookie.path}") // New property for cookie path
    private String refreshTokenCookiePath;


    public UserService(AuthenticationManager authenticationManager, UserRepository iUserRepository,
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
        if (user.getId() == null && user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }
        return iUserRepository.save(user);
    }

    private ResponseCookie generateRefreshTokenCookie(String tokenValue, Long durationMs) {
        return ResponseCookie.from(refreshTokenCookieName, tokenValue)
                .httpOnly(true)
                .secure(true) // Set to true in production (requires HTTPS)
                .path(refreshTokenCookiePath) // Path for the cookie (e.g., "/api/user/refresh" or context path)
                .maxAge(TimeUnit.MILLISECONDS.toSeconds(durationMs)) // Cookie lifetime
                .sameSite("Lax") // Or "Strict". "Lax" is a good default.
                // .domain("yourdomain.com") // Set if frontend and backend are on different subdomains of the same parent domain
                .build();
    }

    private ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "") // Empty value
                .httpOnly(true)
                .secure(true)
                .path(refreshTokenCookiePath)
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                // .domain("yourdomain.com")
                .build();
    }



    public ResponseEntity<AuthResponseDto> registerAndReturnAuthResponse(RegisterDto registerDto) {
        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            // Consider returning a specific error DTO or just status
            return ResponseEntity.status(HttpStatus.SEE_OTHER).body(null); // Indicate email is taken
        }
        User user = new User();
        // UUID will be set by @PrePersist or by saverUser if ID is null
        if (user.getId() == null && user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role role = iRoleRepository.findByRoleName(RoleName.USER);
        user.setRoles(Collections.singletonList(role));
        User savedUser = iUserRepository.save(user);

        String accessToken = jwtUtilities.generateToken(savedUser, Collections.singletonList(role.getRoleName()));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId()); // Existing logic to create and store RT in DB

        ResponseCookie refreshTokenCookie = generateRefreshTokenCookie(refreshToken.getToken(), refreshTokenDurationMs);

        AuthResponseDto authResponseDto = new AuthResponseDto(
                accessToken,
               // Refresh token string is no longer sent in the body
                "Bearer",
                accessTokenExpirationMs,
                savedUser.getEmail(),
                Collections.singletonList(role.getRoleName())
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authResponseDto);
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {

        // For compatibility with existing interface, but ideally interface changes too
        return registerAndReturnAuthResponse(registerDto);
    }


    public AuthResponseDto authenticateAndGenerateTokens(LoginDto loginDto, HttpServletResponse response) {
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

        String accessToken = jwtUtilities.generateToken(user, rolesNames);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Creates and stores RT in DB

        ResponseCookie refreshTokenCookie = generateRefreshTokenCookie(refreshTokenEntity.getToken(), refreshTokenDurationMs);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()); // Add cookie to response

        return new AuthResponseDto(
                accessToken,
               // null, // Refresh token string is no longer sent in the body
                "Bearer",
                accessTokenExpirationMs,
                user.getUsername(),
                rolesNames
        );
    }

    // Keep existing if needed, but it won't set the cookie
    @Override
    public String authenticate(LoginDto loginDto) {
        // This is problematic as it can't set the HttpServletResponse cookie.
        // Controller should call a method that allows setting the cookie.
        // For now, it will just return the access token.
        // Consider deprecating or refactoring this.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        User user = iUserRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> rolesNames = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
        return jwtUtilities.generateToken(user, rolesNames);
    }


    public TokenRefreshResponseDto refreshToken(String requestRefreshTokenFromCookie, HttpServletResponse response) {
        System.out.println("UserService: Attempting to refresh using cookie token."); // Cookie token is not logged for security
        return refreshTokenService.findByToken(requestRefreshTokenFromCookie)
                .map(refreshTokenEntity -> { // Renamed to avoid conflict
                    System.out.println("UserService: Found refresh token entity for user: " + refreshTokenEntity.getUser().getEmail());
                    return refreshTokenService.verifyDeviceAndExpiration(refreshTokenEntity);
                })
                .map(RefreshToken::getUser)
                .map(user -> {
                    System.out.println("UserService: User for new tokens: " + user.getEmail() + " (ID: " + user.getId() + ")");
                    refreshTokenService.deleteByToken(requestRefreshTokenFromCookie); // Delete old RT from DB
                    System.out.println("UserService: Deleted old refresh token from DB.");

                    RefreshToken newRefreshTokenEntity = refreshTokenService.createRefreshToken(user.getId()); // Create new RT in DB
                    System.out.println("UserService: Created new refresh token entity with token string: " + newRefreshTokenEntity.getToken().substring(0, 5) + "...");

                    ResponseCookie newRefreshTokenCookie = generateRefreshTokenCookie(newRefreshTokenEntity.getToken(), refreshTokenDurationMs);
                    response.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString()); // Set new RT cookie

                    List<String> rolesNames = user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    String newAccessToken = jwtUtilities.generateToken(user, rolesNames);

                    return new TokenRefreshResponseDto(
                            newAccessToken,
                           // null, // New refresh token is in the cookie, not body
                            "Bearer",
                            accessTokenExpirationMs
                    );
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshTokenFromCookie, "Refresh token is not in database or invalid!"));
    }

    public ResponseEntity<String> logoutUserAndClearCookie(Integer userId, HttpServletResponse response) {
        System.out.println("UserService: Logging out user ID: " + userId);
        refreshTokenService.deleteByUserId(userId); // Invalidate server-side RT

        ResponseCookie cleanCookie = getCleanRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString()); // Clear client-side cookie

        SecurityContextHolder.clearContext(); // Clear security context
        return ResponseEntity.ok("User logged out successfully.");
    }

    // Overload for existing logoutUser that doesn't return ResponseEntity or modify response
    public void logoutUser(Integer userId) {
        refreshTokenService.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
    }

    // ... (rest of your UserService methods: getAll, create, read, update, delete, etc. are mostly unaffected)
    public List<User> getAll() {
        return iUserRepository.findByDeletedFalse();
    }
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return iUserRepository.save(user);
    }

    public User read(Integer id) {
        return iUserRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public User read(UUID uuid) {
        return iUserRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    public User readByUuid(UUID uuid) {
        return iUserRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }
    public User update(Integer id, User user) {
        User existingUser = iUserRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("User not found for update with id: " + id));
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail()); // Be careful with email updates if unique
        if (user.getPassword() != null && !user.getPassword().isEmpty() && passwordNeedsEncoding(user.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setRoles(user.getRoles()); // Ensure roles are managed entities
        return iUserRepository.save(existingUser);
    }
    public User update(User user) {
        return update(user.getId(), user);
    }
    public boolean delete(Integer id) {
        return iUserRepository.findByIdAndDeletedFalse(id).map(user -> {
            user.setDeleted(true);
            iUserRepository.save(user);
            refreshTokenService.deleteByUserId(id); // Also delete refresh tokens on user soft delete
            return true;
        }).orElse(false);
    }
    public User read(String email) {
        return iUserRepository.findByEmailAndDeletedFalse(email).orElse(null);
    }
    private boolean passwordNeedsEncoding(String password) {
        return password != null && !password.startsWith("$2a$");
    }
}