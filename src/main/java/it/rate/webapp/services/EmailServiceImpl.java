package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import it.rate.webapp.dtos.PasswordResetEmailDTO;
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

  public void sendPasswordReset(PasswordResetEmailDTO dto) {
    SimpleMailMessage smm = new SimpleMailMessage();
    StringBuilder sb = new StringBuilder();
    sb.append("Hi, ").append(dto.username()).append(". \n\n");
    sb.append("We've received a request to reset your password. ");
    sb.append("Please click the link below to complete the reset. \n\n");
    sb.append(environment.getProperty("base.url")).append("/users/reset?");
    sb.append("token=").append(dto.token()).append("&ref=").append(dto.userId()).append("\n\n");
    sb.append(
        "If you need additional assistance, or you did not make this change, please contact: ");
    sb.append(environment.getProperty("ratespot.gmail.username"));

    smm.setFrom(environment.getProperty("spring.mail.username"));
    smm.setTo(dto.email());
    smm.setSubject(dto.subject());
    smm.setText(sb.toString());
    this.mailSender.send(smm);
  }
}
