package it.rate.webapp.services;

import it.rate.webapp.config.Constraints;
import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import it.rate.webapp.repositories.CriterionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class CriterionService {
  private final CriterionRepository criterionRepository;

  public void saveAll(
      @Valid Interest interest,
      @NotEmpty Set<@NotBlank @Length(max = Constraints.MAX_NAME_LENGTH) String> criteriaNames) {
    List<Criterion> criteria =
        criteriaNames.stream()
            .map(c -> Criterion.builder().name(c).interest(interest).build())
            .toList();
    criterionRepository.saveAll(criteria);
  }

  public void updateAll(
      @Valid Interest interest,
      @NotEmpty Set<@NotBlank @Length(max = Constraints.MAX_NAME_LENGTH) String> criteriaNames) {
    // Get old criteria
    List<Criterion> oldCriteria = criterionRepository.findAllByInterest(interest);

    // Collect old criteria names
    Set<String> oldCriteriaNames =
        oldCriteria.stream().map(Criterion::getName).collect(Collectors.toSet());

    // Identify criteria to delete and delete them
    oldCriteria.stream()
        .filter(oldCriterion -> !criteriaNames.contains(oldCriterion.getName()))
        .forEach(criterionRepository::delete);

    // Identify criteria to add and save them
    criteriaNames.stream()
        .filter(name -> !oldCriteriaNames.contains(name))
        .map(name -> Criterion.builder().name(name).interest(interest).build())
        .forEach(criterionRepository::save);
  }
}
