package it.rate.webapp.controllers.api;

import it.rate.webapp.dtos.CriteriaOfPlaceDTO;
import it.rate.webapp.dtos.RatingsDTO;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Place;
import it.rate.webapp.services.PlaceService;
import it.rate.webapp.services.RatingService;
import it.rate.webapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/places")
public class PlaceRestController {
  private final PlaceService placeService;
  private final UserService userService;
  private final RatingService ratingService;

  @PostMapping("/{placeId}/rate")
  @PreAuthorize("@permissionService.ratePlace(#placeId)")
  public ResponseEntity<?> ratePlace(
      @PathVariable Long placeId, @RequestBody RatingsDTO rating, Principal principal) {

    AppUser loggedUser = userService.getByEmail(principal.getName());
    Place place = placeService.getById(placeId);
    ratingService.updateRating(rating, place, loggedUser);

    CriteriaOfPlaceDTO criteria = placeService.getCriteriaOfPlaceDTO(place);

    return ResponseEntity.ok().body(criteria);
  }
}
