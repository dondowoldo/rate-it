package it.rate.webapp.services;

import it.rate.webapp.dtos.*;
import it.rate.webapp.models.*;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Place;
import it.rate.webapp.repositories.PlaceRepository;
import it.rate.webapp.repositories.RatingRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final RatingRepository ratingRepository;
  private final ReviewService reviewService;

  public Optional<Place> findById(Long id) {
    return placeRepository.findById(id);
  }

  public Place getById(Long placeId) {
    return placeRepository.getReferenceById(placeId);
  }

  public Place save(
      @NotNull @Valid PlaceInDTO placeDTO, @Valid Interest interest, @Valid AppUser appUser) {
    Place place = new Place(placeDTO);
    place.setCreator(appUser);
    place.setInterest(interest);

    return placeRepository.save(place);
  }

  public Place update(@Valid Place place, @NotNull @Valid PlaceInDTO placeDTO) {
    place.update(placeDTO);
    return placeRepository.save(place);
  }

  public void addImage(@Valid Place place, String imageId) {
    place.getImageNames().add(imageId);
    placeRepository.save(place);
  }

  public List<PlaceInfoDTO> getPlaceInfoDTOS(@Valid Interest interest) {
    return interest.getPlaces().stream().map(this::getPlaceInfoDTO).collect(Collectors.toList());
  }

  public CriteriaOfPlaceDTO getCriteriaOfPlaceDTO(@Valid Place place) {
    List<CriterionAvgRatingDTO> criteriaAvgRatingDTOs = new ArrayList<>();

    place
        .getInterest()
        .getCriteria()
        .forEach(
            criterion -> criteriaAvgRatingDTOs.add(getCriterionAvgRatingDTO(criterion, place)));

    return new CriteriaOfPlaceDTO(criteriaAvgRatingDTOs);
  }

  public PlaceInfoDTO getPlaceInfoDTO(@Valid Place place) {
    Set<CriterionAvgRatingDTO> criteria =
        place.getInterest().getCriteria().stream()
            .map(criterion -> getCriterionAvgRatingDTO(criterion, place))
            .collect(Collectors.toSet());
    return new PlaceInfoDTO(place, criteria);
  }

  private CriterionAvgRatingDTO getCriterionAvgRatingDTO(Criterion criterion, Place place) {
    OptionalDouble optAvgRating =
        ratingRepository.findAllByCriterionAndPlace(criterion, place).stream()
            .mapToDouble(Rating::getRating)
            .average();

    Double avgRating;

    if (optAvgRating.isPresent()) {
      avgRating = optAvgRating.getAsDouble();
    } else {
      avgRating = null;
    }

    return new CriterionAvgRatingDTO(criterion.getId(), criterion.getName(), avgRating);
  }

  public List<PlaceReviewDTO> getPlaceReviewDTOsByAppUser(AppUser user) {
    List<Review> reviews = reviewService.findAllByAppUser(user);
    List<Rating> ratings = ratingRepository.findAllByAppUser(user);
    List<Place> allPlaces = new ArrayList<>();

    for (Review review : reviews) {
      allPlaces.add(review.getPlace());
    }

    for (Rating rating : ratings) {
      allPlaces.add(rating.getPlace());
    }

    Set<Place> distinctPlacesSet = new HashSet<>(allPlaces);
    List<Place> distinctPlacesList = new ArrayList<>(distinctPlacesSet);

    return distinctPlacesList.stream().map(place -> getPlaceReviewDTO(user, place)).toList();
  }

  //  public PlaceRatingsDTO getPlaceRatingsDTO(Place place) {
  //    Map<AppUser, List<Rating>> ratingsByUser =
  //        place.getRatings().stream().collect(Collectors.groupingBy(Rating::getAppUser));
  //
  //    List<PlaceUserRatingDTO> userRatings =
  //        ratingsByUser.entrySet().stream()
  //            .map(entry -> getSingleUserRatingDTO(entry.getKey(), entry.getValue()))
  //            .collect(Collectors.toList());
  //
  //    return new PlaceRatingsDTO(userRatings);
  //  }

  public List<PlaceReviewDTO> getPlaceReviewDTOsByPlace(Place place) {
    List<Review> reviews = reviewService.findAllByPlace(place);
    List<Rating> ratings = ratingRepository.findAllByPlace(place);
    List<AppUser> allUsers = new ArrayList<>();

    for (Review review : reviews) {
      allUsers.add(review.getAppUser());
    }

    for (Rating rating : ratings) {
      allUsers.add(rating.getAppUser());
    }

    Set<AppUser> distinctUsersSet = new HashSet<>(allUsers);
    List<AppUser> distinctUsersList = new ArrayList<>(distinctUsersSet);

    return distinctUsersList.stream().map(appUser -> getPlaceReviewDTO(appUser, place)).toList();
  }

  private PlaceReviewDTO getPlaceReviewDTO(AppUser user, Place place) {
    Optional<Review> optReview = reviewService.findById(new ReviewId(user.getId(), place.getId()));
    String review = optReview.map(Review::getText).orElse(null);
    List<Rating> ratings = ratingRepository.findAllByAppUserAndPlace(user, place);
    List<RatingDTO> ratingDTOS = ratings.stream().map(RatingDTO::new).toList();
    Double avgRating = null;
    if (!ratings.isEmpty()) {
      avgRating =
          ratingDTOS.stream()
              .mapToDouble(RatingDTO::rating)
              .boxed()
              .collect(Collectors.averagingDouble(Double::doubleValue));
    }
    List<Timestamp> timestamps =
        new ArrayList<>(ratings.stream().map(Rating::getCreatedAt).toList());
    optReview.ifPresent(value -> timestamps.add(value.getCreatedAt()));
    Timestamp latestTimestamp = timestamps.stream().max(Comparator.naturalOrder()).orElse(null);

    return new PlaceReviewDTO(
        user.getUsername(),
        place.getName(),
        place.getId(),
        review,
        ratingDTOS,
        avgRating,
        latestTimestamp);
  }
}

//  public PlaceUserRatingDTO getSingleUserRatingDTO(AppUser user, List<Rating> ratings) {
//
//    Map<String, Double> criterionRatings =
//        ratings.stream()
//            .collect(
//                Collectors.toMap(
//                    rating -> rating.getCriterion().getName(), rating -> rating.getRating() /
// 2.0));
//
//    double averageRating =
//        criterionRatings.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
//
//    if (averageRating != 0.0) {
//      averageRating = Math.round(averageRating * 10.0) / 10.0;
//    }
//
//    return new PlaceUserRatingDTO(user.getUsername(), criterionRatings, averageRating);
//  }
// }
