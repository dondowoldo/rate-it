package it.rate.webapp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import it.rate.webapp.BaseTest;
import it.rate.webapp.dtos.RatingsDTO;
import it.rate.webapp.exceptions.badrequest.InvalidRatingException;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.CriterionRepository;
import it.rate.webapp.repositories.RatingRepository;
import java.util.*;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class RatingServiceTest extends BaseTest {
  @MockBean RatingRepository ratingRepository;
  @MockBean CriterionRepository criterionRepository;
  @Autowired RatingService ratingService;

  @Test
  void updateRatingMissingCriterionInRatings() {
    AppUser appUser = getMockAppUser();
    Place p = getMockPlace();
    Interest i = getMockInterest();

    p.setInterest(i);

    Criterion c1 = Criterion.builder().id(1L).name("Criterion1").build();
    Criterion c2 = Criterion.builder().id(2L).name("Criterion2").build();
    Criterion c3 = Criterion.builder().id(3L).name("Criterion3").build();

    i.setCriteria(Arrays.asList(c1, c2, c3));

    Rating r1 = new Rating(appUser, p, c1, 5);
    Rating r2 = new Rating(appUser, p, c2, 8);

    Map<Long, Integer> ratings = new HashMap<>();
    ratings.put(1L, null);
    ratings.put(3L, 6);

    RatingsDTO ratingsDTO = new RatingsDTO(ratings);

    when(ratingRepository.findById(eq(r1.getId()))).thenReturn(Optional.of(r1));
    when(ratingRepository.findById(eq(r2.getId()))).thenReturn(Optional.of(r2));

    when(ratingRepository.save(any())).thenAnswer(r -> r.getArgument(0));

    when(criterionRepository.findById(eq(c1.getId()))).thenReturn(Optional.of(c1));
    when(criterionRepository.findById(eq(c2.getId()))).thenReturn(Optional.of(c2));
    when(criterionRepository.findById(eq(c3.getId()))).thenReturn(Optional.of(c3));

    assertThrows(
        InvalidRatingException.class, () -> ratingService.updateRating(ratingsDTO, p, appUser));
  }

  @Test
  void updateRatingScoreOutOfRange() {
    AppUser appUser = getMockAppUser();
    Place p = getMockPlace();
    Interest i = getMockInterest();

    p.setInterest(i);

    Criterion c1 = Criterion.builder().id(1L).name("Criterion1").build();
    Criterion c2 = Criterion.builder().id(2L).name("Criterion2").build();
    Criterion c3 = Criterion.builder().id(3L).name("Criterion3").build();

    i.setCriteria(Arrays.asList(c1, c2, c3));

    Rating r1 = new Rating(appUser, p, c1, 5);
    Rating r2 = new Rating(appUser, p, c2, 8);

    Map<Long, Integer> ratings = new HashMap<>();
    ratings.put(1L, null);
    ratings.put(2L, 6);
    ratings.put(3L, 11);

    RatingsDTO ratingsDTO = new RatingsDTO(ratings);

    when(ratingRepository.findById(eq(r1.getId()))).thenReturn(Optional.of(r1));
    when(ratingRepository.findById(eq(r2.getId()))).thenReturn(Optional.of(r2));

    when(ratingRepository.save(any())).thenAnswer(r -> r.getArgument(0));

    when(criterionRepository.findById(eq(c1.getId()))).thenReturn(Optional.of(c1));
    when(criterionRepository.findById(eq(c2.getId()))).thenReturn(Optional.of(c2));
    when(criterionRepository.findById(eq(c3.getId()))).thenReturn(Optional.of(c3));

    assertThrows(
        ConstraintViolationException.class,
        () -> ratingService.updateRating(ratingsDTO, p, appUser));
  }

  @Test
  void updateRatingOk() {
    AppUser appUser = getMockAppUser();
    Place p = getMockPlace();
    Interest i = getMockInterest();

    p.setInterest(i);

    Criterion c1 = Criterion.builder().id(1L).name("Criterion1").build();
    Criterion c2 = Criterion.builder().id(2L).name("Criterion2").build();
    Criterion c3 = Criterion.builder().id(3L).name("Criterion3").build();

    i.setCriteria(Arrays.asList(c1, c2, c3));

    Rating r1 = new Rating(appUser, p, c1, 5);
    Rating r2 = new Rating(appUser, p, c2, 8);

    Map<Long, Integer> ratings = new HashMap<>();
    ratings.put(1L, null);
    ratings.put(2L, 6);
    ratings.put(3L, 4);

    RatingsDTO ratingsDTO = new RatingsDTO(ratings);

    when(ratingRepository.findById(eq(r1.getId()))).thenReturn(Optional.of(r1));
    when(ratingRepository.findById(eq(r2.getId()))).thenReturn(Optional.of(r2));

    when(ratingRepository.save(any())).thenAnswer(r -> r.getArgument(0));

    when(criterionRepository.findById(eq(c1.getId()))).thenReturn(Optional.of(c1));
    when(criterionRepository.findById(eq(c2.getId()))).thenReturn(Optional.of(c2));
    when(criterionRepository.findById(eq(c3.getId()))).thenReturn(Optional.of(c3));

    ratingService.updateRating(ratingsDTO, p, appUser);

    verify(ratingRepository, times(2)).save(any());
    verify(criterionRepository, times(3)).findById(any());
    verify(ratingRepository, times(3)).findById(any());
  }

  private AppUser getMockAppUser() {
    return AppUser.builder()
        .id(1L)
        .username("Lojza")
        .email("lojza@lojza.cz")
        .password("pass")
        .build();
  }

  private Place getMockPlace() {
    return Place.builder()
        .id(1L)
        .name("Place")
        .description("Description")
        .latitude(1.0)
        .longitude(1.0)
        .build();
  }

  private Interest getMockInterest() {
    return Interest.builder().id(1L).name("Interest").description("Description").build();
  }
}
