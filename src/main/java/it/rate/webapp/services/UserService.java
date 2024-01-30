package it.rate.webapp.services;

import it.rate.webapp.config.ServerRole;
import it.rate.webapp.dtos.InterestUserDTO;
import it.rate.webapp.dtos.SignupUserInDTO;
import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.exceptions.badrequest.InvalidUserDetailsException;
import it.rate.webapp.exceptions.badrequest.UserAlreadyExistsException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Validator validator;
  private final AuthenticationManager provider;

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

  public void authenticate(String username, String password, HttpSession session) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
    Authentication authenticated = provider.authenticate(authentication);

    SecurityContextHolder.getContext().setAuthentication(authenticated);

    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());
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
}
