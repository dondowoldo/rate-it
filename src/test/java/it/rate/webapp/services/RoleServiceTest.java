package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.exceptions.badrequest.InvalidRoleDetailsException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.models.RoleId;
import it.rate.webapp.repositories.RoleRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceTest extends BaseTest {
  @MockBean RoleRepository roleRepository;
  @Autowired RoleService roleService;

  Interest i1;
  AppUser u1;

  @BeforeEach
  void setUp() {
    u1 = AppUser.builder().username("Lojza").id(1L).build();

    i1 = Interest.builder().id(1L).build();

    Role r1 = new Role(u1, i1, Role.RoleType.VOTER);

    i1.setRoles(List.of(r1));
  }

  @Test
  void removeRoleInvalidParams() {
    assertThrows(
            ConstraintViolationException.class, () -> roleService.removeRole(null, null));
    assertThrows(
            ConstraintViolationException.class, () -> roleService.removeRole(null, 1L));
    assertThrows(
            ConstraintViolationException.class, () -> roleService.removeRole(1L, null));
  }

  @Test
  void removeRoleRemoveCreatorRoleThrowsException() {
    Role roleToDelete = new Role(u1, i1, Role.RoleType.CREATOR);
    when(roleService.findById(any())).thenReturn(Optional.of(roleToDelete));

    Exception e1 =
            assertThrows(
                    InvalidRoleDetailsException.class, () -> roleService.removeRole(1L, 1L));
    assertEquals("Cannot remove creator role", e1.getMessage());

    verify(roleRepository, never()).deleteById(any());
  }

  @Test
  void removeRoleNonExistentRoleForUser() {
    when(roleService.findById(any())).thenReturn(Optional.empty());
    Exception e4 =
            assertThrows(
                    InvalidRoleDetailsException.class, () -> roleService.removeRole(1L, 1L));

    assertEquals("Role with given details not found", e4.getMessage());
  }

  @Test
  void removeRole() {
    Role roleToDelete = new Role(u1, i1, Role.RoleType.VOTER);
    when(roleRepository.findById(any())).thenReturn(Optional.of(roleToDelete));
    ArgumentCaptor<RoleId> argumentCaptor = ArgumentCaptor.forClass(RoleId.class);

    roleService.removeRole(1L, 1L);

    verify(roleRepository, times(1)).deleteById(argumentCaptor.capture());
    RoleId deletedRole = argumentCaptor.getValue();

    assertEquals(roleToDelete.getId(), deletedRole);
  }
}
