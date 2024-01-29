package it.rate.webapp.services;

import it.rate.webapp.repositories.FollowRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class FollowService {
    private final FollowRepository followRepository;
}
