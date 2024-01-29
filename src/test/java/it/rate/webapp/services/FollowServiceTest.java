//package it.rate.webapp.services;
//
//import it.rate.webapp.BaseTest;
//import it.rate.webapp.config.ServerRole;
//import it.rate.webapp.models.AppUser;
//import it.rate.webapp.models.Interest;
//import it.rate.webapp.models.LikeId;
//import it.rate.webapp.repositories.FollowRepository;
//import it.rate.webapp.repositories.LikeRepository;
//import it.rate.webapp.repositories.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.times;
//
//class FollowServiceTest extends BaseTest {
//
//  @MockBean
//  UserRepository userRepository;
//  @MockBean
//  FollowRepository followRepository;
//
//  @Autowired FollowService followService;
//
//  AppUser u1;
//  AppUser u2;
//
//  @BeforeEach
//  void setUp() {
//    u1 =
//            AppUser.builder()
//                    .id(1L)
//                    .username("Lojza")
//                    .email("lojza@lojza.cz")
//                    .password("Password1")
//                    .serverRole(ServerRole.USER)
//                    .build();
//
//    u2 =
//            AppUser.builder()
//                    .id(2L)
//                    .username("Franta")
//                    .email("franta@franta.cz")
//                    .password("Password1")
//                    .serverRole(ServerRole.USER)
//                    .build();
//  }
//
//  @Test
//  void followOk() {
//    when(userRepository.getReferenceById(any())).thenReturn(u1);
//    when(userRepository.findById(any())).thenReturn(Optional.ofNullable(u2));
//
//    likeService.setLike(u1, i1, true);
//
//    verify(likeRepository, times(1)).save(any());
//  }
//
//  @Test
//  void followBadRequest() {
//    when(userRepository.getReferenceById(any())).thenReturn(u1);
//    when(interestRepository.getReferenceById(any())).thenReturn(i1);
//
//    likeService.setLike(u1, i1, true);
//
//    verify(likeRepository, times(1)).save(any());
//  }
//
//  @Test
//  void unfollow() {
//    when(userRepository.getReferenceById(any())).thenReturn(u1);
//    when(interestRepository.getReferenceById(any())).thenReturn(i1);
//
//    likeService.setLike(u1, i1, false);
//
//    verify(likeRepository, times(1)).deleteById(new LikeId(u1.getId(), i1.getId()));
//  }
//}
