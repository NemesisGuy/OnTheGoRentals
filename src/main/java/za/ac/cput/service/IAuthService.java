package za.ac.cput.service;

import jakarta.servlet.http.HttpServletResponse;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.service.impl.AuthServiceImpl;

/**
 * IAuthService.java
 * Interface defining the contract for authentication-related services.
 * This includes user registration, login (authentication), token refresh, and logout.
 * It orchestrates interactions between user data, token generation/validation,
 * and cookie management for authentication purposes.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-28
 */
public interface IAuthService {

    /**
     * Registers a new user in the system.
     *
     * @param firstName       The first name of the user.
     * @param lastName        The last name of the user.
     * @param email           The email address (must be unique).
     * @param plainPassword   The plain text password (will be encoded).
     * @param defaultRoleName The default role for the new user.
     * @return The created {@link User} entity.
     * @throws za.ac.cput.exception.EmailAlreadyExistsException if the email is already in use.
     * @throws IllegalStateException                            if the default role is not found.
     */
    User registerUser(String firstName, String lastName, String email, String plainPassword, RoleName defaultRoleName);

    /**
     * Authenticates a user and generates access and refresh tokens (the latter as an HTTP-only cookie).
     *
     * @param email               The user's email.
     * @param plainPassword       The user's plain text password.
     * @param httpServletResponse The response object to add the refresh token cookie to.
     * @return An {@link AuthServiceImpl.AuthDetails} object containing the authenticated {@link User},
     * the access token string, and their role names.
     * @throws org.springframework.security.core.AuthenticationException if authentication fails.
     */
    AuthServiceImpl.AuthDetails loginUser(String email, String plainPassword, HttpServletResponse httpServletResponse);

    /**
     * Refreshes an access token using a valid refresh token from an HTTP-only cookie.
     *
     * @param refreshTokenFromCookie The refresh token string.
     * @param httpServletResponse    The response object to add the new refresh token cookie to.
     * @return An {@link AuthServiceImpl.RefreshedTokenDetails} object containing the new access token string.
     * @throws za.ac.cput.exception.TokenRefreshException if the refresh token is invalid or expired.
     */
    AuthServiceImpl.RefreshedTokenDetails refreshAccessToken(String refreshTokenFromCookie, HttpServletResponse httpServletResponse);

    /**
     * Logs out a user by invalidating their refresh tokens and clearing cookies.
     *
     * @param userId              The internal ID of the user logging out.
     * @param httpServletResponse The response object to clear cookies from.
     * @return true if logout was successful, false otherwise.
     */
    boolean logoutUser(int userId, HttpServletResponse httpServletResponse);

    /**
     * Clears authentication-related cookies from the client.
     *
     * @param httpServletResponse The response object.
     */
    void clearAuthCookies(HttpServletResponse httpServletResponse);

    /**
     * Initiates a password reset process by generating a token and sending it to the user's email.
     *
     * @param email The email address of the user requesting the password reset.
     */
    void initiatePasswordReset(String email);


    /**
     * Finalizes the password reset process by validating the token and updating the user's password.
     *
     * @param token       The password reset token sent to the user's email.
     * @param newPassword The new password to set for the user.
     * @throws za.ac.cput.exception.InvalidTokenException if the token is invalid or expired.
     */
    void finalizePasswordReset(String token, String newPassword);

    /**
     * Logs in a user using OAuth2 authentication, typically after validating an ID token from a third-party provider.
     *
     * @param user                The user entity containing information from the OAuth2 provider.
     * @param httpServletResponse The response object to add the refresh token cookie to.
     * @return An {@link AuthServiceImpl.AuthDetails} object containing the authenticated {@link User},
     * the access token string, and their role names.
     */

    AuthServiceImpl.AuthDetails loginUserWithOAuth(User user, HttpServletResponse httpServletResponse);

}