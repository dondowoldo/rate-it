package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import it.rate.webapp.models.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface EmailService {
  void sendEmail(EmailMessageDTO emailMessage);

  void contactUs(EmailMessageDTO emailMessage);

  void sendInvite(@NotNull @Valid Role role);
}
