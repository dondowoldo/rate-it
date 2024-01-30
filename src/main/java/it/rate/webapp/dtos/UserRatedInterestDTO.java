package it.rate.webapp.dtos;

import it.rate.webapp.models.Interest;

import java.util.List;

public record UserRatedInterestDTO(
    Long id, String name, String imageName, List<UserRatedPlaceDTO> ratedPlaces) {

  public UserRatedInterestDTO(Interest interest, List<UserRatedPlaceDTO> ratedPlaces) {
    this(interest.getId(), interest.getName(), interest.getImageName(), ratedPlaces);
  }
}
