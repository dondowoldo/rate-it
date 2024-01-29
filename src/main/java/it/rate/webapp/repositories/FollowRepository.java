package it.rate.webapp.repositories;

import it.rate.webapp.models.Follow;
import it.rate.webapp.models.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {}
