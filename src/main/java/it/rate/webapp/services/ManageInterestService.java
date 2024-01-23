package it.rate.webapp.services;

import it.rate.webapp.exceptions.badrequest.InvalidUserDetailsException;
import it.rate.webapp.exceptions.internalerror.InternalErrorException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.repositories.RoleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
@Validated
@AllArgsConstructor
public class ManageInterestService {
  private final RoleRepository roleRepository;
  private final UserService userService;


  public Role inviteUser(
          @Valid @NotNull Interest interest,
          @NotBlank @Pattern(regexp = "\\b(?:username|email)\\b") String inviteBy,
          @NotBlank String user,
          @NotNull Role.RoleType roleType) {

    AppUser appUser;
    if (inviteBy.equals("username")) {
      appUser =
              userService.findByUsernameIgnoreCase(user).orElseThrow(InvalidUserDetailsException::new);
    } else if (inviteBy.equals("email")) {
      appUser =
              userService.findByEmailIgnoreCase(user).orElseThrow(InvalidUserDetailsException::new);
    } else {
      throw new InternalErrorException("Invalid invitedBy parameter");
    }

    Role role = new Role(appUser, interest, roleType);
    return roleRepository.save(role);
  }
}
