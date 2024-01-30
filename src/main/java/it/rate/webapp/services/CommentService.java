package it.rate.webapp.services;

import it.rate.webapp.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
}
