package it.rate.webapp.dtos;

import it.rate.webapp.config.Constraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PasswordResetDTO(
    @NotBlank String token,
    @NotNull Long ref,
    @NotBlank
        @Pattern(
            regexp = Constraints.PASSWORD_REGEX,
            message =
                "Password needs to be at least 8 characters long and contain at least one uppercase letter and one digit")
        String password) {}
