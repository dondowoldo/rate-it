package it.rate.webapp.services;

import it.rate.webapp.exceptions.badrequest.InvalidInterestDetailsException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.models.RoleId;
import it.rate.webapp.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public void deleteByRoleId(RoleId roleId) {
    roleRepository.deleteById(roleId);
  }

  public Optional<Role> findById(RoleId roleId) {
    return roleRepository.findById(roleId);
  }

  public Role save(Role role) {
    return roleRepository.save(role);
  }

  public void setRole(Interest interest, AppUser appUser, Role.RoleType role) {
    if (interest.isExclusive()) {
      roleRepository.save(new Role(appUser, interest, role));
    } else {
      throw new InvalidInterestDetailsException("Cannot set role for non-exclusive interest");
    }
  }
}
