package it.rate.webapp.dtos;

import java.util.Map;

public record PlaceUserRatingDTO(String userName, Map<String, Double> ratings, double totalAverage) {
}
