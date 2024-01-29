package it.rate.webapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "follows")
public class Follow {

  @EmbeddedId @NotNull private FollowId id;

  @ManyToOne
  @MapsId("followerId")
  private AppUser follower;

  @ManyToOne
  @MapsId("followedId")
  private AppUser followed;

  public Follow(AppUser follower, AppUser followed) {
    this.id = new FollowId(follower.getId(), followed.getId());
    this.follower = follower;
    this.followed = followed;
  }
}
