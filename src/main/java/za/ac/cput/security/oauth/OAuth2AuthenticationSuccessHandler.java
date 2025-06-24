package za.ac.cput.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.service.IAuthService;
import za.ac.cput.service.IUserService;
import za.ac.cput.service.impl.AuthServiceImpl;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final IUserService userService;
    private final IAuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(
            @Lazy IUserService userService,
            @Lazy IAuthService authService // <-- THE FIX IS HERE: Add @Lazy to this dependency as well
    ) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        log.info("OAuth2 Success: Processing user with email '{}'", email);

        User user = userService.processOAuthPostLogin(email, firstName, lastName);

        AuthServiceImpl.AuthDetails authDetails = authService.loginUserWithOAuth(user, response);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", authDetails.getAccessToken())
                .build().toUriString();

        log.info("Redirecting user to frontend with token: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}