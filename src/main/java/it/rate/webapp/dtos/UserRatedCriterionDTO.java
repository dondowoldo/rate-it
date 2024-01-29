package it.rate.webapp.dtos;

import it.rate.webapp.models.Rating;

public record UserRatedCriterionDTO(String name, int rating) {

    public UserRatedCriterionDTO(Rating rating) {
        this(rating.getCriterion().getName(), rating.getRating());
    }
}
