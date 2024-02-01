package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import it.rate.webapp.dtos.PasswordResetDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final Environment environment;

  @Override
  public void sendEmail(EmailMessageDTO emailMessage) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(environment.getProperty("spring.mail.username"));
    smm.setTo(environment.getProperty("ratespot.gmail.username"));
    smm.setSubject(emailMessage.subject());
    smm.setText(emailMessage.text());

    this.mailSender.send(smm);
  }

  @Override
  public void contactUs(EmailMessageDTO emailMessage) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(environment.getProperty("spring.mail.username"));
    smm.setTo(environment.getProperty("ratespot.gmail.username"));
    smm.setSubject(emailMessage.subject());
    smm.setText(emailMessage.from() + ":\n" + emailMessage.text());

    this.mailSender.send(smm);
  }

  public void sendPasswordReset(PasswordResetDTO dto) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(environment.getProperty("spring.mail.username"));
    smm.setTo(dto.email());
    smm.setSubject(dto.subject());
    smm.setText(dto.body() + environment.getProperty("ratespot.gmail.username"));
  }
}
