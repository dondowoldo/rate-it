package it.rate.webapp.dtos;

import it.rate.webapp.config.Constraints;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record InterestInDTO(
    @NotBlank @Length(min = Constraints.MIN_NAME_LENGTH, max = Constraints.MAX_NAME_LENGTH)
        String name,
    @NotBlank @Length(max = Constraints.MAX_DESCRIPTION_LENGTH) String description,
    boolean exclusive,
    String imageName) {}
