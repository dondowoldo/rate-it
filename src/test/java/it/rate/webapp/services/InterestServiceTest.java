package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.repositories.CriterionRepository;
import it.rate.webapp.repositories.InterestRepository;
import it.rate.webapp.repositories.LikeRepository;
import it.rate.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class InterestServiceTest extends BaseTest {

  @MockBean InterestRepository interestRepository;
  @MockBean CriterionRepository criterionRepository;
  @MockBean UserRepository userRepository;
  @MockBean LikeRepository likeRepository;

  @Autowired InterestService interestService;
}