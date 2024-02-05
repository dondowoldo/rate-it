package it.rate.webapp.repositories;

import it.rate.webapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

  List<Rating> findAllByAppUser(AppUser appUser);

  List<Rating> findAllByCriterionAndPlaceAndCriterionDeletedFalse(Criterion criterion, Place place);

  List<Rating> findAllByAppUserAndCriterion_InterestAndCriterionDeletedFalse(
      AppUser appUser, Interest interest);

  List<Rating> findAllByAppUserAndPlaceAndCriterionDeletedFalse(AppUser appUser, Place place);

  List<Rating> findAllByPlaceAndCriterionDeletedFalse(Place place);
}
