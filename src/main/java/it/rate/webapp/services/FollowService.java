package it.rate.webapp.services;

import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.FollowRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class FollowService {
  private final FollowRepository followRepository;

  public void save(@Valid AppUser follower, @Valid AppUser followed) {
    followRepository.save(new Follow(follower, followed));
  }

  public void deleteById(FollowId followId) {
    followRepository.deleteById(followId);
  }

  public String setFollow(@Valid AppUser follower, @Valid AppUser followed, boolean follow)
      throws BadRequestException {
    if (follower.equals(followed)) {
      throw new BadRequestException("User cannot follow themselves");
    }
    if (follow) {
      this.save(follower, followed);
      return "Followed successfully";
    } else {
      this.deleteById(new FollowId(follower.getId(), followed.getId()));
      return "Unfollowed successfully";
    }
  }
}
