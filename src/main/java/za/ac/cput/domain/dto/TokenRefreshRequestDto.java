package za.ac.cput.domain.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank; // For validation


@Data
public class TokenRefreshRequestDto {
    @NotBlank
    private String refreshToken;
}