package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import it.rate.webapp.dtos.PasswordResetEmailDTO;

public interface EmailService {
  void sendEmail(EmailMessageDTO emailMessage);

  void contactUs(EmailMessageDTO emailMessage);






  void sendPasswordReset(PasswordResetEmailDTO dto);
}
