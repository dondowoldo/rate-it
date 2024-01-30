package it.rate.webapp.dtos;

import java.util.List;

public record UserRatedPlaceDTO(Long id, String name, Double avgRating, List<UserRatingDTO> ratedCriteria) {}
