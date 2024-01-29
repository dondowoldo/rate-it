package it.rate.webapp.dtos;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;

import java.util.List;

public record AppUserDTO(
    String username,
    String description,
//    List<String> followers,
//    List<String> followedUsers,
    List<UserRatedInterestDTO> ratedInterests) {

    public AppUserDTO(AppUser user, List<UserRatedInterestDTO> interests) {
        this(
                user.getUsername(),
                user.getDescription(),
//                user.getFollowers(),
//                user.getFollowedUsers(),
                interests
        );
    }
}
