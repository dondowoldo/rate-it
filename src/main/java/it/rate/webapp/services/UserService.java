package it.rate.webapp.services;

import it.rate.webapp.config.ServerRole;
import it.rate.webapp.dtos.AppUserDTO;
import it.rate.webapp.dtos.InterestUserDTO;
import it.rate.webapp.dtos.SignupUserInDTO;
import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.exceptions.badrequest.InvalidUserDetailsException;
import it.rate.webapp.exceptions.badrequest.UserAlreadyExistsException;
import it.rate.webapp.exceptions.unauthorised.ForbiddenOperationException;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Validator validator;

  public Optional<AppUser> findById(Long userId) {
    return userRepository.findById(userId);
  }

  public AppUser getByEmail(String email) {
    return userRepository.getByEmail(email);
  }

  public AppUser getById(Long id) {
    return userRepository.getReferenceById(id);
  }

  public Optional<AppUser> findByUsernameIgnoreCase(String username) {
    return userRepository.findByUsernameIgnoreCase(username);
  }

  public Optional<AppUser> findByEmailIgnoreCase(String email) {
    return userRepository.findByEmailIgnoreCase(email);
  }

  public List<InterestUserDTO> getUsersDTO(
      @Valid Interest interest, @NotNull Role.RoleType roleType) {
    return interest.getRoles().stream()
        .filter(r -> r.getRoleType().equals(roleType))
        .map(InterestUserDTO::new)
        .sorted(Comparator.comparing(dto -> dto.userName().toLowerCase()))
        .collect(Collectors.toList());
  }

  public void registerUser(SignupUserInDTO userDTO) {
    Set<ConstraintViolation<SignupUserInDTO>> violations = validator.validate(userDTO);
    if (!violations.isEmpty()) {
      throw new InvalidUserDetailsException(violations.stream().findFirst().get().getMessage());
    }
    if (userRepository.existsByEmailIgnoreCase(userDTO.email())) {
      throw new UserAlreadyExistsException(
          "User with email " + userDTO.email() + " already exists");
    }
    if (userRepository.existsByUsernameIgnoreCase(userDTO.username())) {
      throw new UserAlreadyExistsException(
          "User with username " + userDTO.username() + " already exists");
    }

    String hashPassword = passwordEncoder.encode(userDTO.password());
    AppUser user =
        AppUser.builder()
            .username(userDTO.username())
            .email(userDTO.email())
            .password(hashPassword)
            .serverRole(ServerRole.USER)
            .build();
    userRepository.save(user);
  }

  public AppUser getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication.getPrincipal().equals("anonymousUser"))) {
      return userRepository.getByEmail(authentication.getName());
    }
    return null;
  }

  public void follow(@Valid AppUser follower, @Valid AppUser followed, boolean follow)
      throws BadRequestException {
    if (follower.equals(followed)) {
      throw new BadRequestException("Users cannot follow themselves!");
    }
    if (follow) {
      follower.getFollows().add(followed);
    } else {
      follower.getFollows().remove(followed);
    }
    userRepository.save(follower);
  }

  public void editUser(@Valid AppUser user, @Valid AppUserDTO editedUser) {
    if (!user.getId().equals(editedUser.id())) {
      throw new ForbiddenOperationException("Users cannot edit each other's details!");
    }
    user.setBio(editedUser.bio());
    userRepository.save(user);
  }
}
