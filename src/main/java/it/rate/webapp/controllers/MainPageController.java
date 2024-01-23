package it.rate.webapp.controllers;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.services.InterestService;
import it.rate.webapp.services.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainPageController {

  private final InterestService interestService;
  private final UserService userService;

  @GetMapping({"/", "/index"})
  public String index(Model model, Principal principal) {

    if (principal != null) {
      AppUser loggedUser = userService.getByEmail(principal.getName());
      model.addAttribute("loggedUser", loggedUser);
      model.addAttribute("likedInterests", interestService.findAllLikedByAppUser(loggedUser));
    }

    return "main/index";
  }
}