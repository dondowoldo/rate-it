package it.rate.webapp.dtos;

import it.rate.webapp.models.Place;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record UserRatedPlaceDTO(
    Long placeId, String placeName, Double avgRating, List<RatingDTO> ratings) {

  public UserRatedPlaceDTO(Place place, List<RatingDTO> ratings) {
    this(
        place.getId(),
        place.getName(),
        ratings.stream()
            .mapToDouble(RatingDTO::rating)
            .boxed()
            .collect(Collectors.averagingDouble(Double::doubleValue)),
        ratings.stream()
            .sorted(Comparator.comparingInt(RatingDTO::rating).reversed())
            .collect(Collectors.toList()));
  }
}
