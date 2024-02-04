package it.rate.webapp.repositories;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Place;
import it.rate.webapp.models.Review;
import it.rate.webapp.models.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    List<Review> findAllByPlace(Place place);

    List<Review> findAllByAppUser(AppUser user);
}
