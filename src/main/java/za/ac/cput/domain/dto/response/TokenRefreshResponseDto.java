package za.ac.cput.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
    // private String refreshToken; // Send new refresh token if rotating  // REMOVED - new one is in HttpOnly cookie
    private String tokenType = "Bearer";
    private Long accessTokenExpiresIn;
}