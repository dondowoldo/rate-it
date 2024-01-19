package it.rate.webapp.repositories;

import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CriterionRepository extends JpaRepository<Criterion, Long> {
  Set<Criterion> findAllByInterest(Interest interest);
}
