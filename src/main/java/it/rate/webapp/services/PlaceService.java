package it.rate.webapp.services;

import it.rate.webapp.dtos.*;
import it.rate.webapp.models.*;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Place;
import it.rate.webapp.repositories.PlaceRepository;
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
  private final RatingService ratingService;
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
        ratingService.findAllByCriterionAndPlace(criterion, place).stream()
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

  public List<PlaceReviewDTO> getPlaceReviewDTOs(
      @Valid AppUser user, @Valid Interest interest, Comparator<PlaceReviewDTO> comparator) {
    List<Review> reviews = reviewService.findAllByAppUserAndInterest(user, interest);
    List<Rating> ratings = ratingService.findAllByAppUserAndInterest(user, interest);
    Set<Place> distinctPlaces = new HashSet<>();

    for (Review review : reviews) {
      distinctPlaces.add(review.getPlace());
    }

    for (Rating rating : ratings) {
      distinctPlaces.add(rating.getPlace());
    }

    return distinctPlaces.stream()
        .map(place -> getPlaceReviewDTO(user, place))
        .sorted(comparator)
        .toList();
  }

  public List<PlaceReviewDTO> getPlaceReviewDTOs(@Valid Place place) {
    List<Review> reviews = reviewService.findAllByPlace(place);
    List<Rating> ratings = ratingService.findAllByPlace(place);
    Set<AppUser> distinctUsers = new HashSet<>();

    for (Review review : reviews) {
      distinctUsers.add(review.getAppUser());
    }

    for (Rating rating : ratings) {
      distinctUsers.add(rating.getAppUser());
    }

    return distinctUsers.stream()
        .map(appUser -> getPlaceReviewDTO(appUser, place))
        .sorted(Comparator.comparing(PlaceReviewDTO::timestamp).reversed())
        .toList();
  }

  private PlaceReviewDTO getPlaceReviewDTO(AppUser user, Place place) {
    Optional<Review> optReview = reviewService.findById(new ReviewId(user.getId(), place.getId()));
    String review = optReview.map(Review::getText).orElse(null);
    List<Rating> ratings = ratingService.findAllByAppUserAndPlace(user, place);
    List<RatingDTO> ratingDTOS = ratings.stream().map(RatingDTO::new).toList();

    Double avgRating =
        ratingDTOS.stream()
            .mapToDouble(RatingDTO::rating)
            .boxed()
            .collect(Collectors.averagingDouble(Double::doubleValue));

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
