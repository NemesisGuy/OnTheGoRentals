package za.ac.cput.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
    private String refreshToken; // Send new refresh token if rotating
    private String tokenType = "Bearer";
    private Long accessTokenExpiresIn;
}