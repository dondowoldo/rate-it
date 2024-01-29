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
}
