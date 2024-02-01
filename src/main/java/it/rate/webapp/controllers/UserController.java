package it.rate.webapp.controllers;

import it.rate.webapp.dtos.AppUserDTO;
import it.rate.webapp.dtos.SignupUserInDTO;
import it.rate.webapp.dtos.SignupUserOutDTO;
import it.rate.webapp.dtos.UserRatedInterestDTO;
import it.rate.webapp.exceptions.badrequest.BadRequestException;
import it.rate.webapp.exceptions.notfound.UserNotFoundException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.services.RatingService;
import it.rate.webapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final RatingService ratingService;

  @GetMapping("/signup")
  public String signupPage() {
    return "user/signupForm";
  }

  @PostMapping("/signup")
  public String newUser(
      SignupUserInDTO userDTO, Model model, String confirmPassword, HttpServletRequest request) {
    if (!confirmPassword.equals(userDTO.password())) {
      model.addAttribute("error", "Passwords do not match. Please try again.");
      model.addAttribute("userDTO", new SignupUserOutDTO(userDTO));
      return "user/signupForm";
    }

    try {
      userService.registerUser(userDTO);
    } catch (BadRequestException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("userDTO", new SignupUserOutDTO(userDTO));
      return "user/signupForm";
    }

    userService.authenticate(userDTO.email(), userDTO.password(), request.getSession(true));
    return "redirect:/";
  }

  @GetMapping("/login")
  public String loginPage(Principal principal) {
    if (principal != null) {
      return "redirect:/";
    }
    return "user/loginForm";
  }

  @GetMapping("/{username}")
  public String userPage(@PathVariable String username, Model model, Principal principal) {
    AppUser user =
        userService.findByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);
    List<UserRatedInterestDTO> ratedInterests = ratingService.getAllUserRatedInterestDTOS(user);
    if (principal != null) {
      model.addAttribute("loggedUser", userService.getByEmail(principal.getName()));
    }
    model.addAttribute("user", new AppUserDTO(user));
    model.addAttribute("ratedInterests", ratedInterests);
    return "user/page";
  }

  @GetMapping("/{username}/interests/{interestId}")
  public String interestDetail(
      @PathVariable String username,
      @PathVariable Long interestId,
      Model model,
      Principal principal) {

    AppUser user = userService.findByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);

    model.addAttribute("user", new AppUserDTO(user));

    return "user/interest";
  }
}
