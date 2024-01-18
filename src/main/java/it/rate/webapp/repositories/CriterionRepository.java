package it.rate.webapp.repositories;

import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface CriterionRepository extends JpaRepository<Criterion, Long> {
  Set<Criterion> findAllByInterest(Interest interest);

  @Transactional
  void deleteByNameAndInterest(String name, Interest interest);
}
