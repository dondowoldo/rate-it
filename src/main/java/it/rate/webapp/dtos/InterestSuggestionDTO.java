package it.rate.webapp.dtos;

import it.rate.webapp.models.Interest;

public record InterestSuggestionDTO(
    Long id,
    String name,
    String description,
    Long likes,
    Long places,
    String imageName,
    Double distanceKm) {

  public InterestSuggestionDTO(Interest interest, Double distanceKm) {
    this(
        interest.getId(),
        interest.getName(),
        interest.getDescription(),
        (long) interest.countLikes(),
        (long) interest.countPlaces(),
        interest.getImageName(),
        distanceKm);
  }
}
