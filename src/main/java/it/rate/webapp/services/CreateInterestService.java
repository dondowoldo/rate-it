package it.rate.webapp.services;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.repositories.CriterionRepository;
import it.rate.webapp.repositories.InterestRepository;
import it.rate.webapp.repositories.RoleRepository;
import it.rate.webapp.repositories.UserRepository;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
@AllArgsConstructor
public class CreateInterestService {

  private InterestRepository interestRepository;
  private CriterionRepository criterionRepository;
  private UserRepository userRepository;
  private RoleRepository roleRepository;

  public Interest save(
      @Valid Interest interest,
      @NotEmpty List<@NotBlank String> receivedCriteria) {

    List<Criterion> criteria =
        receivedCriteria.stream().map(c -> Criterion.builder().name(c).build()).toList();

    criterionRepository.saveAll(criteria);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    AppUser currentUser = userRepository.getByEmail(authentication.getName());

    interestRepository.save(interest);
    criteria.forEach(c -> c.setInterest(interest));

    Role newRole = new Role(currentUser, interest, Role.RoleType.CREATOR);
    interest.setRoles(List.of(newRole));
    roleRepository.save(newRole);

    return interest;
  }
}
