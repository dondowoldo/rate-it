package it.rate.webapp.dtos;

import it.rate.webapp.models.Place;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record UserRatedPlaceDTO(
    Long id, String name, Double avgRating, List<UserRatingDTO> ratedCriteria) {

  public UserRatedPlaceDTO(Place place, List<UserRatingDTO> ratedCriteria) {
    this(
        place.getId(),
        place.getName(),
        ratedCriteria.stream().mapToDouble(UserRatingDTO::rating).average().orElse(0.0) / 2,
        ratedCriteria.stream()
            .sorted(Comparator.comparingDouble(UserRatingDTO::rating).reversed())
            .collect(Collectors.toList()));
  }
}
