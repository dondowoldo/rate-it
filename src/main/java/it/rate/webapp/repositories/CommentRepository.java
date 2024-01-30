package it.rate.webapp.repositories;

import it.rate.webapp.models.Comment;
import it.rate.webapp.models.CommentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {}
