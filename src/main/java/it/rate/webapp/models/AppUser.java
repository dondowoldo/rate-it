package it.rate.webapp.models;

import it.rate.webapp.config.Constraints;
import it.rate.webapp.config.ServerRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.apache.tomcat.util.bcel.Const;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class AppUser {
  @Id @GeneratedValue @NotNull private Long id;

  @NotBlank
  @Length(min = Constraints.MIN_USERNAME_LENGTH, max = Constraints.MAX_USERNAME_LENGTH)
  @Column(nullable = false, unique = true)
  private String username;

  @NotBlank
  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank
  @Pattern(regexp = Constraints.PASSWORD_REGEX)
  @Length(max = Constraints.MAX_VARCHAR_LENGTH)
  @Column(nullable = false)
  private String password;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ServerRole serverRole;

  @OneToMany(mappedBy = "creator")
  @Builder.Default
  private List<Place> createdPlaces = new ArrayList<>();

  @OneToMany(mappedBy = "appUser", fetch = FetchType.EAGER)
  @Builder.Default
  private List<Role> roles = new ArrayList<>();

  @OneToMany(mappedBy = "appUser")
  @Builder.Default
  private List<Like> likes = new ArrayList<>();

  @OneToMany(mappedBy = "appUser")
  @Builder.Default
  private List<Rating> ratings = new ArrayList<>();

  @Length(max = Constraints.MAX_VARCHAR_LENGTH)
  private String description;
}
