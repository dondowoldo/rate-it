package it.rate.webapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
  public Category(String name) {
    this.name = name;
  }

  @Id @GeneratedValue @NotNull private Long id;
  @NotBlank private String name;

  @ManyToMany(mappedBy = "categories")
  private List<Interest> interests = new ArrayList<>();
}
