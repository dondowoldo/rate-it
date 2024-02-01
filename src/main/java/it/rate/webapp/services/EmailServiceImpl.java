package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
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

  @Override
  public void sendInvite(@NotNull @Valid Role role) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(environment.getProperty("spring.mail.username"));
    smm.setTo(role.getAppUser().getEmail());
    smm.setSubject("New invitation");
    smm.setText(
        String.format(
            """
                              Hi, %s!,\s

                              You have been invited to join %s community.\s

                              Visit the interest here : %s
                              """,
            role.getAppUser().getUsername(),
            role.getInterest().getName(),
            "https://ratespot.app/interests/" + role.getInterest().getId()));
    this.mailSender.send(smm);
  }
}
