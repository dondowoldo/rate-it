package it.rate.webapp.controllers.api;

import it.rate.webapp.BaseIntegrationTest;
import it.rate.webapp.repositories.RatingRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsersRestControllerTest extends BaseIntegrationTest {

  @Autowired MockMvc mockMvc;

  // when RatingRepository is not mocked, the test will fail when running ./gradlew check
  @MockBean
  private RatingRepository ratingRepository;

  @Test
  @Transactional
  @Rollback
  @WithMockUser(
      username = "alfonz@alfonz.cz",
      authorities = {"USER"})
  void followUserOk() throws Exception {
    String requestBody = "{\"follow\": true}";

    mockMvc
        .perform(
            post("/api/v1/users/1/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk());
  }
}
