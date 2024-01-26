package it.rate.webapp.config;

import org.springframework.stereotype.Component;

@Component
public class Constraints {
  public static final int MIN_NAME_LENGTH = 3;
  public static final int MAX_NAME_LENGTH = 25;
  public static final int MIN_DESCRIPTION_LENGTH = 10;
  public static final int MAX_DESCRIPTION_LENGTH = 1000;
  public static final int MIN_PASSWORD_LENGTH = 8;
  public static final int MAX_PASSWORD_LENGTH = 255;
  public static final int MIN_USERNAME_LENGTH = 3;
  public static final int MAX_USERNAME_LENGTH = 25;
  public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$";
}
