package it.rate.webapp.services;

import it.rate.webapp.dtos.CoordinatesDTO;
import it.rate.webapp.dtos.InterestSuggestionDTO;
import it.rate.webapp.dtos.InterestUserDTO;
import it.rate.webapp.dtos.LikedInterestsDTO;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;
  private final RoleRepository roleRepository;

  public Optional<Interest> findById(Long interestId) {
    return interestRepository.findById(interestId);
  }

  public Interest getById(Long interestId) {
    return interestRepository.getReferenceById(interestId);
  }

  public List<Interest> findAllSortByLikes() {
    return interestRepository.findAllSortByLikes();
  }

  public List<Interest> getLikedInterests(AppUser appUser) {
    return interestRepository.findAllByLikes_AppUser(appUser);
  }

  public Interest save(@Valid Interest interest) {
    return interestRepository.save(interest);
  }

  public Interest saveNew(@Valid Interest interest, AppUser appUser) {
    interestRepository.save(interest);

    Role creator = new Role(appUser, interest, Role.RoleType.CREATOR);
    interest.getRoles().add(creator);
    roleRepository.save(creator);

    return interest;
  }

  public List<InterestSuggestionDTO> getAllSuggestionDTOS() {
    return findAllSortByLikes().stream()
        .map(interest -> new InterestSuggestionDTO(interest, null))
        .collect(Collectors.toList());
  }

  public List<InterestSuggestionDTO> getAllSuggestionDTOS(CoordinatesDTO usersCoords) {
    return findAllSortByLikes().stream()
        .map(
            interest ->
                new InterestSuggestionDTO(
                    interest, getDistanceToNearestPlace(usersCoords, interest.getPlaces())))
        .sorted(Comparator.comparingDouble(InterestSuggestionDTO::distanceKm))
        .collect(Collectors.toList());
  }

  public List<LikedInterestsDTO> getLikedInterestsDTOS(AppUser loggedUser) {
    return interestRepository.findAllByLikes_AppUser(loggedUser).stream()
        .sorted(Comparator.comparing(i -> i.getName().toLowerCase()))
        .map(LikedInterestsDTO::new)
        .collect(Collectors.toList());
  }

  public List<InterestUserDTO> getUsersDTO(Interest interest, @NotNull Role.RoleType role) {
    return interest.getRoles().stream()
        .filter(r -> r.getRole().equals(role))
        .map(InterestUserDTO::new)
        .sorted(Comparator.comparing(dto -> dto.userName().toLowerCase()))
        .collect(Collectors.toList());
  }

  private double getDistanceToNearestPlace(CoordinatesDTO usersCoords, List<Place> places) {
    double minDistance = Double.MAX_VALUE;
    for (Place place : places) {
      double distance =
          haversine(
              usersCoords.latitude(),
              usersCoords.longitude(),
              place.getLatitude(),
              place.getLongitude());
      minDistance = Math.min(minDistance, distance);
    }
    return minDistance;
  }

  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    // Haversine formula
    // https://www.geeksforgeeks.org/program-distance-two-points-earth/#:~:text=by%20Aayush%20Chaturvedi-,Java,-//%20Java%20program%20to
    lon1 = Math.toRadians(lon1);
    lat1 = Math.toRadians(lat1);
    lon2 = Math.toRadians(lon2);
    lat2 = Math.toRadians(lat2);

    // Haversine formula
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    double a =
        Math.pow(Math.sin(dlat / 2), 2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
    double c = 2 * Math.asin(Math.sqrt(a));
    // Radius of earth in kilometers.
    final double r = 6371;
    return (c * r); // Distance in kilometers
  }
}
