package it.rate.webapp.dtos;

import it.rate.webapp.models.AppUser;

public record PasswordResetDTO(String email, String username, String subject, String body) {
  public PasswordResetDTO(AppUser user, String token) {
    this(
        user.getEmail(),
        user.getUsername(),
        "Reset your RateSpot account password",
        String.format(
            "Hi, %s. \n\n"
                + "We've received a request to reset your password. "
                + "Please click the link below to complete the reset. \n\n"
                + "http://localhost:8080/users/reset?token=%s \n\n"
                + "If you need additional assistance, or you did not make this change, "
                + "please contact: ",
            user.getUsername(), token));
  }
}
