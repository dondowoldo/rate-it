package it.rate.webapp.services;

import it.rate.webapp.config.Constraints;
import it.rate.webapp.models.*;
import it.rate.webapp.repositories.CommentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;

  public void deleteById(CommentId commentId) {
    commentRepository.deleteById(commentId);
  }

  public void save(
      @NotBlank @Length(max = Constraints.MAX_VARCHAR_LENGTH) String text,
      @Valid Place place,
      @Valid AppUser loggedUser) {
    CommentId commentId = new CommentId(loggedUser.getId(), place.getId());
    Optional<Comment> optComment = commentRepository.findById(commentId);

    if (optComment.isPresent()) {
      commentRepository.deleteById(commentId);
    }
    commentRepository.save(new Comment(loggedUser, place, text));
  }
}
