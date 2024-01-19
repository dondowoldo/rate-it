package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.LikeId;
import it.rate.webapp.models.Place;
import it.rate.webapp.repositories.CriterionRepository;
import it.rate.webapp.repositories.InterestRepository;
import it.rate.webapp.repositories.LikeRepository;
import it.rate.webapp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LikeServiceTest extends BaseTest {

  @MockBean InterestRepository interestRepository;
  @MockBean CriterionRepository criterionRepository;
  @MockBean UserRepository userRepository;
  @MockBean LikeRepository likeRepository;

  @Autowired LikeService likeService;

  @Test
  void changeLikeValueCreateLike() {
    Interest i = getMockInterest();
    AppUser u = getMockAppUser();

    when(userRepository.getReferenceById(any())).thenReturn(u);
    when(interestRepository.getReferenceById(any())).thenReturn(i);

    likeService.setLike(u, i, true);

    verify(likeRepository, times(1)).save(any());
  }

  @Test
  void changeLikeValueDeleteLike() {
    Interest i = getMockInterest();
    AppUser u = getMockAppUser();

    when(userRepository.getReferenceById(any())).thenReturn(u);
    when(interestRepository.getReferenceById(any())).thenReturn(i);

    likeService.setLike(u, i, false);

    verify(likeRepository, times(1)).deleteById(new LikeId(u.getId(), i.getId()));
  }

  private AppUser getMockAppUser() {
    return AppUser.builder()
            .id(1L)
            .username("Lojza")
            .email("lojza@lojza.cz")
            .password("pass")
            .build();
  }

  private Interest getMockInterest() {
    return Interest.builder().id(1L).name("Interest").description("Description").build();
  }
}
