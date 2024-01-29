package it.rate.webapp.controllers.api;

import it.rate.webapp.dtos.FollowDTO;
import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.services.FollowService;
import it.rate.webapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersRestController {
  private final UserService userService;
  private final FollowService followService;

  @PostMapping("/{userId}/follow")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ResponseEntity<?> followUser(
      @PathVariable Long userId, @RequestBody FollowDTO follow, Principal principal) {

    AppUser loggedUser = userService.getByEmail(principal.getName());
    Optional<AppUser> userToFollow = userService.findById(userId);

    if (userToFollow.isEmpty()) {
      return ResponseEntity.badRequest().body("This user doesn't exist");
    }

    try {
      return ResponseEntity.ok()
          .body(followService.setFollow(loggedUser, userToFollow.get(), follow.follow()));
    } catch (BadRequestException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
