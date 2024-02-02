package it.rate.webapp.controllers.api;

import it.rate.webapp.dtos.PlaceInfoDTO;
import it.rate.webapp.dtos.RatingsDTO;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.ReviewId;
import it.rate.webapp.models.Place;
import it.rate.webapp.services.ReviewService;
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
  private final ReviewService reviewService;

  @PostMapping("/{placeId}/rate")
  @PreAuthorize("@permissionService.ratePlace(#placeId)")
  public ResponseEntity<?> ratePlace(
      @PathVariable Long placeId, @RequestBody RatingsDTO rating, Principal principal) {

    AppUser loggedUser = userService.getByEmail(principal.getName());
    Place place = placeService.getById(placeId);
    ratingService.save(rating, place, loggedUser);
    PlaceInfoDTO placeInfoDTO = placeService.getPlaceInfoDTO(place);

    return ResponseEntity.ok().body(placeInfoDTO);
  }

  @PostMapping("/{placeId}/comment")
  @PreAuthorize("@permissionService.ratePlace(#placeId)")
  public ResponseEntity<?> placeReview(
      @PathVariable Long placeId, String review, Principal principal) {

    AppUser loggedUser = userService.getByEmail(principal.getName());
    Place place = placeService.getById(placeId);
    reviewService.save(review, place, loggedUser);

    return ResponseEntity.ok().body("Comment saved");
  }

  @DeleteMapping("/{placeId}/delete-comment")
  @PreAuthorize("@permissionService.ratePlace(#placeId)")
  public ResponseEntity<?> deleteReview(@PathVariable Long placeId, Principal principal) {

    AppUser loggedUser = userService.getByEmail(principal.getName());
    Place place = placeService.getById(placeId);
    reviewService.deleteById(new ReviewId(loggedUser.getId(), place.getId()));

    return ResponseEntity.ok().body("Comment deleted");
  }
}
