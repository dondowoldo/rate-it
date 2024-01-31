package it.rate.webapp.dtos;

import it.rate.webapp.models.Review;

public record ReviewDTO(String text, String date) {

  public ReviewDTO(Review review) {
    this(review.getText(), review.getCreatedAt().toString());
  }
}
