package it.rate.webapp.repositories;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  @Query(
      "SELECT i, COUNT(l) AS likesCount "
          + "FROM Interest i "
          + "LEFT JOIN i.likes l "
          + "GROUP BY i.id "
          + "ORDER BY likesCount DESC")
  List<Interest> findAllSortByLikes();

  List<Interest> findAllByLikes_AppUser(AppUser appUser);

  @Query("SELECT DISTINCT i FROM Interest i " +
          "INNER JOIN Place p ON i.id = p.interest.id " +
          "INNER JOIN Rating r ON p.id = r.place.id " +
          "WHERE r.appUser.id = :userId")
  List<Interest> findInterestsRatedByUser(Long userId);
}