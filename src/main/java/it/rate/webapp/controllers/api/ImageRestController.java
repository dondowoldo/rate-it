package it.rate.webapp.controllers.api;

import it.rate.webapp.dtos.ImageUploadResponseDTO;
import it.rate.webapp.exceptions.api.ApiServiceUnavailableException;
import it.rate.webapp.models.Interest;
import it.rate.webapp.services.GoogleImageService;
import it.rate.webapp.services.InterestService;
import java.io.IOException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageRestController {

  private final GoogleImageService googleImageService;
  private final InterestService interestService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getImage(@PathVariable String id) {

    try {
      return ResponseEntity.ok().body(googleImageService.getImageById(id));
    } catch (ApiServiceUnavailableException e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping("/new-interest-image")
  @PreAuthorize("@permissionService.canCreateInterest()")
  public ResponseEntity<?> uploadNewInterestImage(@RequestParam("picture") MultipartFile file, Principal principal) {

    String userName = principal.getName();
    try {
      return ResponseEntity.ok()
          .body(new ImageUploadResponseDTO(googleImageService.saveImage(file, userName)));
    } catch (IOException e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping("/interests/{interestId}/edit")
  @PreAuthorize("@permissionService.manageCommunity(#interestId)")
  public ResponseEntity<?> changeInterestImage(
      @RequestParam("picture") MultipartFile file, @PathVariable Long interestId, Principal principal) {

    Interest interest = interestService.getById(interestId);
    String userName = principal.getName();

    try {
      return ResponseEntity.ok()
          .body(
              new ImageUploadResponseDTO(googleImageService.changeInterestImage(interest, file, userName)));
    } catch (ApiServiceUnavailableException e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
