package it.rate.webapp.dtos;

import it.rate.webapp.models.Place;

import java.util.List;
import java.util.stream.Collectors;

public record UserRatedPlaceDTO(
        Long placeId, String placeName, Double avgRating, List<RatingDTO> ratings) {

  public UserRatedPlaceDTO(Place place, List<RatingDTO> ratedCriteria) {
    this(
            place.getId(),
            place.getName(),
            ratedCriteria.stream()
                    .mapToDouble(RatingDTO::rating)
                    .boxed()
                    .collect(Collectors.averagingDouble(Double::doubleValue)),
            ratedCriteria);
  }
}
