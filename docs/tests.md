Summary of the Fixes that Led to Success:

@WebMvcTest Setup:

Using @WebMvcTest(AuthController.class) to focus on the controller.

@Import(SpringSecurityConfig.class) to load your actual security rules.

@MockBean for all direct service dependencies of AuthController (IAuthService).

@MockBean for dependencies required by SpringSecurityConfig or its components (like JwtAuthenticationFilter) that are
not available in the @WebMvcTest slice:

JwtUtilities

CustomerUserDetailsService

CSRF Handling:

Initially, we thought 403s were due to CSRF and tried adding .with(csrf()).

However, your SpringSecurityConfig has .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/auth/**")). This means for
paths under /api/v1/auth/, CSRF protection is disabled by your configuration. Therefore, .with(csrf()) was not needed
for these paths in the tests. The 403s and 302s were due to other security mechanisms or context loading issues. Once
those were resolved, removing .with(csrf()) for the auth paths was the correct approach for those specific tests. For
other POST/PUT/DELETE endpoints not ignored by CSRF, .with(csrf()) would be essential.

Mocking authService Methods:

Ensuring that when(authService.loginUser(...)).thenReturn(...) and similar stubs correctly matched the arguments and
returned the AuthDetails or RefreshedTokenDetails objects with the structure expected by the controller.

Matching the constructor signatures for AuthDetails and RefreshedTokenDetails when creating mock return values.

Controller Logic for Optional Cookie (refreshToken):

Changing @CookieValue(name = "...") String refreshTokenFromCookie to @CookieValue(name = "...", required = false) String
refreshTokenFromCookie in the AuthController#refreshToken method allowed the controller to handle the missing cookie
case gracefully and return a 401, rather than Spring MVC throwing a MissingRequestCookieException earlier (which led to
a 500 from the global handler).

JSONPath Assertions:

This was the final hurdle. The andDo(print()) output revealed that your successful responses for /register, /login, and
/refresh (the ones returning AuthResponseDto and TokenRefreshResponseDto from the controller) were being wrapped into an
ApiResponse structure, likely by a ResponseBodyAdvice or your GlobalExceptionHandler when it formats successful
responses.

The JSONPaths were corrected from $.fieldName to $.data.fieldName to match this actual wrapped structure. For
example, $.accessToken became $.data.accessToken.

The logout endpoint already returned ApiResponse<String>, so its $.data JSONPath was correct.

The refreshToken_shouldReturnUnauthorized_whenRefreshTokenIsMissing test was updated to check the fields of the wrapped
ApiResponse for a 401 status.

You've successfully navigated a complex testing scenario involving Spring MVC, Spring Security, Mockito, and JSON
response structures. The systematic debugging, especially using andDo(print()) and carefully analyzing the logs, paid
off.

Congratulations on getting all the tests to pass! Your wife will be pleased.