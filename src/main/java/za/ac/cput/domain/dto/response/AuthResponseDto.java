package za.ac.cput.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    // private String refreshToken; // REMOVED - now in HttpOnly cookie
    private String tokenType = "Bearer";
    private Long accessTokenExpiresIn; // Optional: inform client about access token expiry
    private String email; // Or username
    private List<String> roles; // Optional: client might need this
}