package it.rate.webapp.dtos;

import it.rate.webapp.models.AppUser;

public record PasswordResetEmailDTO(String email, String username, String subject, String body) {
  public PasswordResetEmailDTO(AppUser user, String token) {
    this(
        user.getEmail(),
        user.getUsername(),
        "Reset your RateSpot account password",
        String.format(
            "Hi, %s. \n\n"
                + "We've received a request to reset your password. "
                + "Please click the link below to complete the reset. \n\n"
                + "http://localhost:8080/users/reset?token=%s&ref=%s \n\n"
                + "If you need additional assistance, or you did not make this change, "
                + "please contact: ",
            user.getUsername(), token, user.getId()));
  }
}
