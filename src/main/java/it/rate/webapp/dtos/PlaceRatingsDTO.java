package it.rate.webapp.dtos;

import java.util.HashSet;
import java.util.List;

public record PlaceRatingsDTO(List<PlaceUserRatingDTO> userPlaceRatings) {

  // for testing purposes
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    PlaceRatingsDTO that = (PlaceRatingsDTO) object;

    if (this.userPlaceRatings.size() != that.userPlaceRatings.size()) {
      return false;
    }

    return new HashSet<>(this.userPlaceRatings).containsAll(that.userPlaceRatings);
  }
}
