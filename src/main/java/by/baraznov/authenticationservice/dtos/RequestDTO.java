package by.baraznov.authenticationservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestDTO(
        @NotNull
        Integer id,
        @Size(min = 4, max = 30, message = "The login must contain between 4 and 30 characters")
        @NotBlank(message = "The login mustn't be empty")
        String login,
        @Size(min = 4, max = 30, message = "The password must contain between 4 and 30 characters")
        @NotBlank(message = "The password mustn't be empty")
        String password
) {
}
