package it.rate.webapp.dtos;

import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;

import java.util.List;

public record AppUserDTO(String username, String description, int followers, int follows) {

    public AppUserDTO(AppUser appUser) {
        this(appUser.getUsername(), appUser.getBio(), appUser.getFollowers().size(), appUser.getFollows().size());
    }
}
