package it.rate.webapp.services;

import it.rate.webapp.dtos.RatingsDTO;
import it.rate.webapp.dtos.UserRatedInterestDTO;
import it.rate.webapp.dtos.UserRatedPlaceDTO;
import it.rate.webapp.dtos.UserRatingDTO;
import it.rate.webapp.exceptions.badrequest.InvalidCriterionDetailsException;
import it.rate.webapp.exceptions.badrequest.InvalidRatingException;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.CriterionRepository;
import it.rate.webapp.repositories.RatingRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class RatingService {

  private final RatingRepository ratingRepository;
  private final CriterionRepository criterionRepository;

  public RatingsDTO getUsersRatingsDto(@Valid AppUser appUser, @Valid Place place) {
    List<Rating> ratings = ratingRepository.findAllByAppUserAndPlace(appUser, place);
    return new RatingsDTO(ratings);
  }

  public void updateRating(
      @NotNull @Valid RatingsDTO ratings, @Valid Place place, @Valid AppUser appUser) {
    Set<Criterion> ratedCriteria = validateRatings(ratings, place);

    ratings
        .ratings()
        .forEach(
            (criterionId, rating) -> {
              Criterion criterion =
                  ratedCriteria.stream()
                      .filter(c -> Objects.equals(c.getId(), criterionId))
                      .findAny()
                      .orElseThrow(InvalidCriterionDetailsException::new);
              updateOrCreateRating(appUser, place, criterion, rating);
            });
  }

  private void updateOrCreateRating(
      AppUser appUser, Place place, Criterion criterion, Integer rating) {
    RatingId ratingId = new RatingId(appUser.getId(), place.getId(), criterion.getId());
    Optional<Rating> optRating = ratingRepository.findById(ratingId);

    if (rating == null) {
      if (optRating.isPresent()) {
        ratingRepository.deleteById(ratingId);
      }
      return;
    }

    if (optRating.isPresent()) {
      Rating existingRating = optRating.get();
      existingRating.setRating(rating);
      ratingRepository.save(existingRating);
    } else {
      Rating newRating = new Rating(appUser, place, criterion, rating);
      ratingRepository.save(newRating);
    }
  }

  private Set<Criterion> validateRatings(RatingsDTO ratings, Place place) {
    List<Criterion> placeCriteria = place.getInterest().getCriteria();
    Set<Criterion> ratedCriteria = new HashSet<>();

    ratings
        .ratings()
        .forEach(
            (criterionId, rating) -> {
              Criterion criterion = getCriterion(criterionId);
              ratedCriteria.add(criterion);
            });
    if (!ratedCriteria.containsAll(placeCriteria)) {
      throw new InvalidRatingException();
    }

    return ratedCriteria;
  }

  private Criterion getCriterion(Long criterionId) {
    return criterionRepository
        .findById(criterionId)
        .orElseThrow(InvalidCriterionDetailsException::new);
  }

  public List<UserRatedInterestDTO> getAllUserRatedInterestDTOS(AppUser appUser) {
    List<Rating> userRatings = ratingRepository.findAllByAppUser(appUser);

    Map<Interest, Map<Place, List<Rating>>> ratingsByInterestAndPlace =
        userRatings.stream()
            .collect(
                Collectors.groupingBy(
                    rating -> rating.getPlace().getInterest(),
                    Collectors.groupingBy(Rating::getPlace, Collectors.toList())));

    return ratingsByInterestAndPlace.entrySet().stream()
        .map(
            entry -> {
              Interest interest = entry.getKey();
              List<UserRatedPlaceDTO> userRatedPlaceDTOs =
                  entry.getValue().entrySet().stream()
                      .map(
                          placeEntry -> {
                            Place place = placeEntry.getKey();
                            List<UserRatingDTO> userRatingDTOs =
                                placeEntry.getValue().stream()
                                    .map(
                                        userRating ->
                                            new UserRatingDTO(
                                                userRating.getRating(),
                                                userRating.getCriterion().getName()))
                                    .collect(Collectors.toList());

                            return new UserRatedPlaceDTO(
                                place,
                                userRatingDTOs);
                          })
                      .sorted(Comparator.comparingDouble(UserRatedPlaceDTO::avgRating).reversed())
                      .collect(Collectors.toList());

              return new UserRatedInterestDTO(
                  interest,
                  userRatedPlaceDTOs);
            })
        .sorted(Comparator.comparingInt(UserRatedInterestDTO::likes).reversed())
        .toList();
  }
}
