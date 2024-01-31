package it.rate.webapp.services;

import it.rate.webapp.dtos.EmailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final String rateSpotEmail;
  private final String rateSpotGmail;

  @Autowired
  public EmailServiceImpl(
      JavaMailSender mailSender,
      @Value("${spring.mail.username}") String rateSpotEmail,
      @Value("${ratespot.gmail.address}") String rateSpotGmail) {
    this.mailSender = mailSender;
    this.rateSpotEmail = rateSpotEmail;
    this.rateSpotGmail = rateSpotGmail;
  }

  @Override
  public void sendEmail(EmailMessageDTO emailMessage) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(rateSpotEmail);
    smm.setTo(rateSpotGmail);
    smm.setSubject(emailMessage.subject());
    smm.setText(emailMessage.text());

    this.mailSender.send(smm);
  }

  @Override
  public void contactUs(EmailMessageDTO emailMessage) {
    SimpleMailMessage smm = new SimpleMailMessage();
    smm.setFrom(rateSpotEmail);
    smm.setTo(rateSpotGmail);
    smm.setSubject(emailMessage.subject());
    smm.setText(emailMessage.from() + ":\n" + emailMessage.text());

    this.mailSender.send(smm);
  }
}
