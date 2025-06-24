package za.ac.cput.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // This method is called by Spring Security after fetching the user's info from Google.
        // We delegate to the default implementation to get the standard user object.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // You could add custom logic here if needed, but for our flow,
        // the main logic will be in the AuthenticationSuccessHandler.
        // We just return the user object as is.
        return oAuth2User;
    }
}