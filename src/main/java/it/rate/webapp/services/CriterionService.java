package it.rate.webapp.services;

import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import it.rate.webapp.repositories.CriterionRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

@Service
@Validated
@AllArgsConstructor
public class CriterionService {
  private final CriterionRepository criterionRepository;

  public void createNew(Interest interest, @NotEmpty List<@NotBlank String> criteriaNames) {
    List<Criterion> criteria =
        criteriaNames.stream()
            .map(c -> Criterion.builder().name(c).interest(interest).build())
            .toList();
    criterionRepository.saveAll(criteria);
  }

  public void updateExisting(
      Interest interest, @NotEmpty List<@NotBlank String> criteriaNames) {
    Set<Criterion> oldCriteria = criterionRepository.findAllByInterest(interest);
    List<String> oldCriteriaNames = oldCriteria.stream().map(Criterion::getName).toList();

    List<Criterion> newCriteria =
        criteriaNames.stream()
            .filter(name -> !oldCriteriaNames.contains(name))
            .map(name -> Criterion.builder().name(name).build())
            .toList();

    for (String name : oldCriteriaNames) {
      if (!criteriaNames.contains(name)) {
        criterionRepository.deleteByNameAndInterest(name, interest);
      }
    }
    newCriteria.forEach(c -> c.setInterest(interest));

    criterionRepository.saveAll(newCriteria);
  }
}