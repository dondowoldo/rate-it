package it.rate.webapp.controllers.api;

import it.rate.webapp.services.GoogleImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageRestController {

  private final GoogleImageService googleImageService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getImage(@PathVariable String id) {

    try {
      return ResponseEntity.ok().body(googleImageService.getImageById(id));
    } catch (IOException e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
