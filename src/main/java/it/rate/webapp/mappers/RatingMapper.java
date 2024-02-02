package it.rate.webapp.mappers;

import it.rate.webapp.dtos.UserRatedInterestDTO;
import it.rate.webapp.dtos.UserRatedPlaceDTO;
import it.rate.webapp.dtos.RatingDTO;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Place;
import it.rate.webapp.models.Rating;

import java.util.List;
import java.util.stream.Collectors;

public class RatingMapper {

  public static RatingDTO remapToUserRatingDTO(Rating rating) {
    return new RatingDTO(rating.getRating(), rating.getCriterion().getName());
  }

  public static UserRatedPlaceDTO remapToUserRatedPlaceDTO(
      Place place, List<Rating> ratings) {
    List<RatingDTO> ratingDTOS =
        ratings.stream()
            .map(RatingMapper::remapToUserRatingDTO)
            .collect(Collectors.toList());

    return new UserRatedPlaceDTO(place, ratingDTOS);
  }

  public static UserRatedInterestDTO remapToUserRatedInterestDTO(
      Interest interest, List<UserRatedPlaceDTO> ratedPlacesDTOs) {

    return new UserRatedInterestDTO(interest, ratedPlacesDTOs);
  }
}
