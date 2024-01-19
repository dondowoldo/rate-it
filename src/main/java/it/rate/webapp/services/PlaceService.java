package it.rate.webapp.services;

import it.rate.webapp.dtos.CriteriaOfPlaceDTO;
import it.rate.webapp.dtos.CriterionAvgRatingDTO;
import it.rate.webapp.dtos.PlaceInfoDTO;
import it.rate.webapp.models.*;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Place;
import it.rate.webapp.repositories.PlaceRepository;
import it.rate.webapp.repositories.RatingRepository;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final RatingRepository ratingRepository;

  public Optional<Place> findById(Long id) {
    return placeRepository.findById(id);
  }

  public Place getById(Long placeId) {
    return placeRepository.getReferenceById(placeId);
  }

  public Place save(Place place, @Valid Interest interest, @Valid AppUser appUser) {
    place.setCreator(appUser);
    place.setInterest(interest);

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

  private PlaceInfoDTO getPlaceInfoDTO(Place place) {
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
}
