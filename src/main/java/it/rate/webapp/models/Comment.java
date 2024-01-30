package it.rate.webapp.models;

import it.rate.webapp.config.Constraints;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

  @EmbeddedId @NotNull private CommentId id;

  @NotBlank
  @Length(max = Constraints.MAX_VARCHAR_LENGTH)
  @Column(nullable = false)
  private String text;

  @NotNull
  @Column(nullable = false)
  private final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

  @ManyToOne
  @MapsId("userId")
  private AppUser appUser;

  @ManyToOne
  @MapsId("placeId")
  private Place place;

  public Comment(AppUser appUser, Place place, String text) {
    this.id = new CommentId(appUser.getId(), place.getId());
    this.appUser = appUser;
    this.place = place;
    this.text = text;
  }
}
