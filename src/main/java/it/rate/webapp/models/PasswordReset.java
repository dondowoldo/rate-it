package it.rate.webapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordReset {
  @Id @GeneratedValue private Long id;

  @NotBlank
  @Column(nullable = false)
  private String token;

  @OneToOne private AppUser user;

  public PasswordReset(AppUser user, String token) {
    this.user = user;
    this.token = token;
  }
}
