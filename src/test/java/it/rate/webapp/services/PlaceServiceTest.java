package it.rate.webapp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.rate.webapp.BaseTest;
import it.rate.webapp.config.ServerRole;
import it.rate.webapp.dtos.*;
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
  Interest i1;
  AppUser u1;
  AppUser u2;
  Place p1;

  @BeforeEach
  void setupAuthentication() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("joe");

    SecurityContextHolder.setContext(securityContext);
  }

  @BeforeEach
  void setUp() {
    u1 =
        AppUser.builder()
            .id(1L)
            .username("Lojza")
            .email("lojza@lojza.cz")
            .password("Password1")
            .serverRole(ServerRole.USER)
            .build();
    u2 =
        AppUser.builder()
            .id(2L)
            .username("Franta")
            .email("franta@franta.cz")
            .password("Password1")
            .serverRole(ServerRole.USER)
            .build();
    i1 = Interest.builder().id(1L).name("Interest").description("Description").build();
    p1 =
        Place.builder()
            .id(1L)
            .name("Place")
            .description("Description")
            .latitude(1.0)
            .longitude(1.0)
            .build();
  }

  @Test
  void saveNewPlace() throws BadRequestException {
    // Mock the placeRepository to return whatever Place object it receives
    when(placeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    PlaceInDTO placeDTO =
        new PlaceInDTO(
            p1.getName(),
            p1.getAddress(),
            p1.getDescription(),
            p1.getLatitude(),
            p1.getLongitude());
    // Call the method under test
    Place res = placeService.save(placeDTO, i1, u1);

    // Assertions to verify the results
    assertSame(res.getCreator(), u1);
    assertSame(res.getInterest(), i1);

    // Verify that placeRepository.save() was called exactly once with the same Place object used in
    // the test
    verify(placeRepository, times(1)).save(same(res));
  }

  @Test
  void getPlaceInfoDTOSHappyCase() {
    List<Criterion> criteria =
        Arrays.asList(
            Criterion.builder().id(1L).name("Criterion1").build(),
            Criterion.builder().id(2L).name("Criterion2").build());

    List<Rating> ratings =
        Arrays.asList(
            new Rating(u1, p1, criteria.get(0), 3),
            new Rating(u1, p1, criteria.get(1), 4),
            new Rating(u2, p1, criteria.get(0), 5),
            new Rating(u2, p1, criteria.get(1), 6));

    CriterionAvgRatingDTO criterionAvgRatingOne =
        new CriterionAvgRatingDTO(criteria.get(1).getId(), criteria.get(1).getName(), 5D);
    CriterionAvgRatingDTO criterionAvgRatingTwo =
        new CriterionAvgRatingDTO(criteria.get(0).getId(), criteria.get(0).getName(), 4D);

    i1.setPlaces(List.of(p1));
    p1.setRatings(ratings);
    p1.setInterest(i1);
    i1.setCriteria(criteria);

    List<PlaceInfoDTO> expectedResult =
        List.of(new PlaceInfoDTO(p1, Set.of(criterionAvgRatingOne, criterionAvgRatingTwo)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), p1))
        .thenReturn(Arrays.asList(ratings.get(0), ratings.get(2)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(1), p1))
        .thenReturn(Arrays.asList(ratings.get(1), ratings.get(3)));

    List<PlaceInfoDTO> actualResult = placeService.getPlaceInfoDTOS(i1);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getPlaceInfoDTOSNoCriteria() {
    i1.setPlaces(List.of(p1));
    p1.setInterest(i1);

    List<PlaceInfoDTO> expectedResult = List.of(new PlaceInfoDTO(p1, Set.of()));

    assertEquals(expectedResult, placeService.getPlaceInfoDTOS(i1));
  }

  @Test
  void getCriteriaOfPlaceDtoHappyCase() {
    List<Criterion> criteria = Arrays.asList(new Criterion(), new Criterion());

    List<Rating> ratings =
        Arrays.asList(
            new Rating(u1, p1, criteria.get(0), 3),
            new Rating(u1, p1, criteria.get(1), 4),
            new Rating(u2, p1, criteria.get(0), 5),
            new Rating(u2, p1, criteria.get(1), 6));

    p1.setRatings(ratings);
    p1.setInterest(i1);
    i1.setCriteria(criteria);

    CriteriaOfPlaceDTO expectedResult =
        new CriteriaOfPlaceDTO(
            Arrays.asList(
                new CriterionAvgRatingDTO(criteria.get(0).getId(), criteria.get(0).getName(), 4D),
                new CriterionAvgRatingDTO(criteria.get(1).getId(), criteria.get(1).getName(), 5D)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), p1))
        .thenReturn(Arrays.asList(ratings.get(0), ratings.get(2)));

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(1), p1))
        .thenReturn(Arrays.asList(ratings.get(1), ratings.get(3)));

    CriteriaOfPlaceDTO actualResult = placeService.getCriteriaOfPlaceDTO(p1);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getCriteriaOfPlaceDtoNoRatings() {
    List<Criterion> criteria = List.of(Criterion.builder().id(1L).name("Criterion1").build());
    p1.setInterest(i1);
    i1.setCriteria(criteria);

    when(ratingRepository.findAllByCriterionAndPlace(criteria.get(0), p1))
        .thenReturn(new ArrayList<>());

    CriteriaOfPlaceDTO expectedResult =
        new CriteriaOfPlaceDTO(
            List.of(
                new CriterionAvgRatingDTO(
                    criteria.get(0).getId(), criteria.get(0).getName(), null)));

    CriteriaOfPlaceDTO actualResult = placeService.getCriteriaOfPlaceDTO(p1);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getSingleUserRatingDtoHappyCase() {
    List<Criterion> criteria = Arrays.asList(new Criterion(), new Criterion());
    criteria.get(0).setName("Mnamovatost");
    criteria.get(1).setName("Pohlednost");

    List<Rating> ratings =
        Arrays.asList(
            new Rating(u1, p1, criteria.get(0), 3), new Rating(u1, p1, criteria.get(1), 4));

    p1.setRatings(ratings);

    Map<String, Double> criterionRatingsResult = new HashMap<>();
    criterionRatingsResult.put("Mnamovatost", 1.5);
    criterionRatingsResult.put("Pohlednost", 2.0);

    PlaceUserRatingDTO expectedResult =
        new PlaceUserRatingDTO("Lojza", criterionRatingsResult, 1.8);

    PlaceUserRatingDTO actualResult = placeService.getSingleUserRatingDTO(u1, ratings);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);
  }

  @Test
  void getPlaceUserRatingDtoWithMultipleUsersAndRatings() {

    List<Criterion> criteria = Arrays.asList(new Criterion(), new Criterion());
    criteria.get(0).setName("Mnamovatost");
    criteria.get(1).setName("Pohlednost");

    List<Rating> ratings =
        Arrays.asList(
            new Rating(u1, p1, criteria.get(0), 3),
            new Rating(u1, p1, criteria.get(1), 4),
            new Rating(u2, p1, criteria.get(0), 2),
            new Rating(u2, p1, criteria.get(1), 8));

    p1.setRatings(ratings);

    Map<String, Double> criterionRatingsResultUserOne = new HashMap<>();
    criterionRatingsResultUserOne.put("Mnamovatost", 1.5);
    criterionRatingsResultUserOne.put("Pohlednost", 2.0);

    Map<String, Double> criterionRatingsResultUserTwo = new HashMap<>();
    criterionRatingsResultUserTwo.put("Mnamovatost", 1.0);
    criterionRatingsResultUserTwo.put("Pohlednost", 4.0);

    PlaceUserRatingDTO userOneRatingDto =
        new PlaceUserRatingDTO("Lojza", criterionRatingsResultUserOne, 1.8);
    PlaceUserRatingDTO userTwoRatingDto =
        new PlaceUserRatingDTO("Franta", criterionRatingsResultUserTwo, 2.5);

    List<PlaceUserRatingDTO> expectedListOfUserRatings = List.of(userOneRatingDto, userTwoRatingDto);

    PlaceAllUsersRatingsDTO expectedResult = new PlaceAllUsersRatingsDTO(expectedListOfUserRatings);

    PlaceAllUsersRatingsDTO actualResult = placeService.getPlaceUserRatingDto(p1);

    assertNotNull(actualResult);
    assertEquals(actualResult, expectedResult);

  }
}
