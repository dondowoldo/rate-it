package it.rate.webapp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.rate.webapp.BaseTest;
import it.rate.webapp.dtos.CriteriaOfPlaceDTO;
import it.rate.webapp.dtos.CriterionAvgRatingDTO;
import it.rate.webapp.dtos.PlaceInfoDTO;
import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.PlaceRepository;
import it.rate.webapp.repositories.RatingRepository;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class PlaceServiceTest extends BaseTest {

  @MockBean Authentication authentication;
  @MockBean SecurityContext securityContext;
  @MockBean InterestService interestService;
  @MockBean UserService userService;
  @MockBean PlaceRepository placeRepository;
  @MockBean RatingRepository ratingRepository;

  @Autowired PlaceService placeService;

  @BeforeEach
  void setupAuthentication() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("joe");

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void saveNewPlace() throws BadRequestException {
    // Prepare test data and mock responses
    Place place = getMockPlace();
    AppUser creator = getMockAppUser();
    Interest interest = getMockInterest();

    // Mock the placeRepository to return whatever Place object it receives
    when(placeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Call the method under test
    Place res = placeService.save(place, interest, creator);

    // Assertions to verify the results
    assertSame(res.getCreator(), creator);
    assertSame(res.getInterest(), interest);

    // Verify that placeRepository.save() was called exactly once with the same Place object used in
    // the test
    verify(placeRepository, times(1)).save(same(place));
  }

  @Test
  void getPlaceInfoDTOSHappyCase() {
    Place place = getMockPlace();
    Interest interest = getMockInterest();
    AppUser userOne = getMockAppUser();
    AppUser userTwo = getMockAppUser();

    List<Criterion> criteria =
        Arrays.asList(
            Criterion.builder().id(1L).name("Criterion1").build(),
            Criterion.builder().id(2L).name("Criterion2").build());

    List<Rating> ratings =
        Arrays.asList(
            new Rating(userOne, place, criteria.get(0), 3),
            new Rating(userOne, place, criteria.get(1), 4),
            new Rating(userTwo, place, criteria.get(0), 5),
            new Rating(userTwo, place, criteria.get(1), 6));

    CriterionAvgRatingDTO criterionAvgRatingOne =
        new CriterionAvgRatingDTO(criteria.get(1).getId(), criteria.get(1).getName(), 5D);
    CriterionAvgRatingDTO criterionAvgRatingTwo =
        new CriterionAvgRatingDTO(criteria.get(0).getId(), criteria.get(0).getName(), 4D);

    interest.setPlaces(List.of(place));
    place.setRatings(ratings);
    place.setInterest(interest);
    interest.setCriteria(criteria);

    List<PlaceInfoDTO> expectedResult =
        List.of(new PlaceInfoDTO(place, Set.of(criterionAvgRatingOne, criterionAvgRatingTwo)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), place))
        .thenReturn(Arrays.asList(ratings.get(0), ratings.get(2)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(1), place))
        .thenReturn(Arrays.asList(ratings.get(1), ratings.get(3)));

    List<PlaceInfoDTO> actualResult = placeService.getPlaceInfoDTOS(interest);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getPlaceInfoDTOSNoCriteria() {
    Place place = getMockPlace();
    Interest interest = getMockInterest();

    interest.setPlaces(List.of(place));
    place.setInterest(interest);

    List<PlaceInfoDTO> expectedResult = List.of(new PlaceInfoDTO(place, Set.of()));

    assertEquals(expectedResult, placeService.getPlaceInfoDTOS(interest));
  }

  @Test
  void getCriteriaOfPlaceDtoHappyCase() {
    Place place = getMockPlace();
    Interest interest = getMockInterest();
    AppUser userOne = getMockAppUser();
    AppUser userTwo = getMockAppUser();

    List<Criterion> criteria = Arrays.asList(new Criterion(), new Criterion());

    List<Rating> ratings =
        Arrays.asList(
            new Rating(userOne, place, criteria.get(0), 3),
            new Rating(userOne, place, criteria.get(1), 4),
            new Rating(userTwo, place, criteria.get(0), 5),
            new Rating(userTwo, place, criteria.get(1), 6));

    place.setRatings(ratings);
    place.setInterest(interest);
    interest.setCriteria(criteria);

    CriteriaOfPlaceDTO expectedResult =
        new CriteriaOfPlaceDTO(
            Arrays.asList(
                new CriterionAvgRatingDTO(criteria.get(0).getId(), criteria.get(0).getName(), 4D),
                new CriterionAvgRatingDTO(criteria.get(1).getId(), criteria.get(1).getName(), 5D)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), place))
        .thenReturn(Arrays.asList(ratings.get(0), ratings.get(2)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(1), place))
        .thenReturn(Arrays.asList(ratings.get(1), ratings.get(3)));

    CriteriaOfPlaceDTO actualResult = placeService.getCriteriaOfPlaceDTO(place);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getCriteriaOfPlaceDtoNoRatings() {
    Place place = getMockPlace();
    Interest interest = getMockInterest();
    List<Criterion> criteria = List.of(Criterion.builder().id(1L).name("Criterion1").build());
    place.setInterest(interest);
    interest.setCriteria(criteria);

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), place))
        .thenReturn(new ArrayList<>());

    CriteriaOfPlaceDTO expectedResult =
        new CriteriaOfPlaceDTO(
            List.of(
                new CriterionAvgRatingDTO(
                    criteria.get(0).getId(), criteria.get(0).getName(), null)));

    CriteriaOfPlaceDTO actualResult = placeService.getCriteriaOfPlaceDTO(place);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
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
