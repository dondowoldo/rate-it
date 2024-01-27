package it.rate.webapp.models;

import it.rate.webapp.config.Constraints;
import it.rate.webapp.dtos.PlaceInDTO;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Getter
@Setter
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "places")
public class Place {
  @Id @GeneratedValue @NotNull private Long id;

  @NotBlank
  @Length(min = Constraints.MIN_NAME_LENGTH, max = Constraints.MAX_NAME_LENGTH)
  @Column(nullable = false)
  private String name;

  @Length(max = Constraints.MAX_DESCRIPTION_LENGTH)
  @Column(length = 1000)
  private String description;

  @Length(max = Constraints.MAX_VARCHAR_LENGTH)
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

  public Place(@NotNull @Valid PlaceInDTO placeDTO) {
    this.name = placeDTO.name();
    this.address = placeDTO.address();
    this.description = placeDTO.description();
    this.latitude = placeDTO.latitude();
    this.longitude = placeDTO.longitude();
  }

  public Double getAverageRating() {
    Double averageRating = null;
    OptionalDouble optAverageRating = ratings.stream().mapToDouble(Rating::getRating).average();
    if (optAverageRating.isPresent()) {
      averageRating = optAverageRating.getAsDouble();
    }
    return averageRating;
  }

  public void update(@NotNull @Valid PlaceInDTO placeDTO) {
    this.name = placeDTO.name();
    this.address = placeDTO.address();
    this.description = placeDTO.description();
    this.latitude = placeDTO.latitude();
    this.longitude = placeDTO.longitude();
  }
}
