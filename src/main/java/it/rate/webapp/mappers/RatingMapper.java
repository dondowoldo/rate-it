package it.rate.webapp.mappers;

import it.rate.webapp.dtos.UserRatedPlaceDTO;
import it.rate.webapp.dtos.UserRatingDTO;
import it.rate.webapp.models.Place;
import it.rate.webapp.models.Rating;
import java.util.List;
import java.util.stream.Collectors;

public class RatingMapper {
  public static UserRatedPlaceDTO remapToUserRatedPlaceDTO(Place place, List<Rating> criteriaRating) {
    List<UserRatingDTO> userRatingDTOs =
        criteriaRating.stream()
            .map(RatingMapper::remapToUserRatingDTO)
            .collect(Collectors.toList());

    return new UserRatedPlaceDTO(
        place,
        userRatingDTOs);
  }

  public static UserRatingDTO remapToUserRatingDTO(Rating userRating) {
    return new UserRatingDTO(
        userRating.getRating(),
        userRating.getCriterion().getName());
  }
}
