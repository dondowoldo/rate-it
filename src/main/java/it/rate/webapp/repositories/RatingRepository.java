package it.rate.webapp.repositories;

import it.rate.webapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
  List<Rating> findAllByAppUserAndPlace(AppUser appUser, Place place);

  List<Rating> findAllByCriterionAndPlace(Criterion criterion, Place place);

  @Query(
      "SELECT DISTINCT r FROM Rating r "
          + "JOIN FETCH r.criterion c "
          + "WHERE r.appUser = :appUser AND (c.deleted = false)")
  List<Rating> findAllNotDeletedRatingsByUser(AppUser appUser);

  @Query(
      "SELECT DISTINCT r FROM Interest i "
          + "LEFT JOIN i.criteria c "
          + "LEFT JOIN i.places p "
          + "LEFT JOIN c.ratings cr "
          + "LEFT JOIN p.ratings pr "
          + "LEFT JOIN Rating r ON r IN (cr, pr) "
          + "WHERE i.id = :interestId AND r.appUser.id = :userId AND c.deleted = false")
  List<Rating> findAllNotDeletedByAppUserAndInterest(AppUser appUser, Interest interest);
}
