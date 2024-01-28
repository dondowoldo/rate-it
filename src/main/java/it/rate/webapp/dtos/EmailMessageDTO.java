package it.rate.webapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EmailMessageDTO(
        @NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email address") String from,
        @NotBlank(message = "Email cannot be empty") @Email(message = "Invalid email address") String to,
        @NotBlank(message = "Subject cannot be empty") @Length(min = 3, max = 25) String subject,
        @NotBlank(message = "Message cannot be empty") @Length(max = 1000) String text) {
}