package it.rate.webapp.controllers.api;

import it.rate.webapp.services.FollowService;
import it.rate.webapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")

public class UsersRestController {
    private final UserService userService;
    private final FollowService followService;

  @PostMapping("/{userId}/follow")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
  public ResponseEntity<?> followUser(@PathVariable Long userId, Principal principal) {
        return ResponseEntity.ok().body("Not implemented yet");
    }
}
