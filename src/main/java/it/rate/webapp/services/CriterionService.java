package it.rate.webapp.services;

import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import it.rate.webapp.repositories.CriterionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
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

  public void createNew(@Valid Interest interest, @NotEmpty List<@NotBlank String> criteriaNames) {
    List<Criterion> criteria =
        criteriaNames.stream()
            .map(c -> Criterion.builder().name(c).interest(interest).build())
            .toList();
    criterionRepository.saveAll(criteria);
  }

  public void updateExisting(
      @Valid Interest interest, @NotEmpty List<@NotBlank String> criteriaNames) {
    // Get old criteria
    Set<Criterion> oldCriteria = criterionRepository.findAllByInterest(interest);

    // Collect old criteria names
    Set<String> oldCriteriaNames =
        oldCriteria.stream().map(Criterion::getName).collect(Collectors.toSet());

    // Identify criteria to delete and delete them
    Set<Criterion> criteriaToDelete =
        oldCriteria.stream()
            .filter(oldCriterion -> !criteriaNames.contains(oldCriterion.getName()))
            .collect(Collectors.toSet());
    criterionRepository.deleteAll(criteriaToDelete);

    // Identify criteria to add and save them
    Set<Criterion> newCriteria =
        criteriaNames.stream()
            .filter(name -> !oldCriteriaNames.contains(name))
            .map(name -> Criterion.builder().name(name).interest(interest).build())
            .collect(Collectors.toSet());
    criterionRepository.saveAll(newCriteria);
  }
}
