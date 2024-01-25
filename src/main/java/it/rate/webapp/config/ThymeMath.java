package it.rate.webapp.config;

import org.springframework.stereotype.Component;

@Component
public class ThymeMath {
  public double roundRating(double a) {
    return Math.round((a / 2) * 10) / 10.0;
  }
}
