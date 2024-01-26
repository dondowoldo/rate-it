package it.rate.webapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "places")
public class Place {
  @Id @GeneratedValue @NotNull private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @Column(length = 1000)
  private String description;

  private String address;

  @NotNull
  @Column(nullable = false)
  @Range(min = -90, max = 90)
  private Double latitude;

  @NotNull
  @Column(nullable = false)
  @Range(min = -180, max = 180)
  private Double longitude;

  @ElementCollection @Builder.Default private List<String> imageNames = new ArrayList<>();

  @Builder.Default private boolean deleted = false;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private AppUser creator;

  @ManyToOne private Interest interest;

  @OneToMany(mappedBy = "place")
  @Builder.Default
  private List<Rating> ratings = new ArrayList<>();

  public Double getAverageRating() {
    Double averageRating = null;
    OptionalDouble optAverageRating = ratings.stream().mapToDouble(Rating::getRating).average();
    if (optAverageRating.isPresent()) {
      averageRating = optAverageRating.getAsDouble();
    }
    return averageRating;
  }
}
