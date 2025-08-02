package by.baraznov.authenticationservice.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResponseDTO(
        String accessToken,
        String refreshToken
) {
}
