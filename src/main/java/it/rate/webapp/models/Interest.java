package it.rate.webapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interests")
public class Interest {
  @Id @GeneratedValue @NotNull private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Column(nullable = false)
  private String description;

  @Builder.Default private boolean deleted = false;
  private boolean exclusive;

  private String imageName;

  @OneToMany(mappedBy = "interest")
  @Builder.Default
  private List<Place> places = new ArrayList<>();

  @OneToMany(mappedBy = "interest")
  @Builder.Default
  private List<Criterion> criteria = new ArrayList<>();

  @OneToMany(mappedBy = "interest")
  @Builder.Default
  private List<Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "interest")
  @Builder.Default
  private List<Role> roles = new ArrayList<>();

  @ManyToMany
  @Builder.Default
  @JoinTable(
      name = "interest_category",
      joinColumns = @JoinColumn(name = "interest_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private List<Category> categories = new ArrayList<>();

  public int countLikes() {
    return likes.size();
  }
}
