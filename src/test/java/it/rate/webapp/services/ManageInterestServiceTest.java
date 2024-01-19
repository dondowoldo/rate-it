package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.exceptions.badrequest.InvalidInterestDetailsException;
import it.rate.webapp.exceptions.badrequest.InvalidUserDetailsException;
import it.rate.webapp.models.AppUser;
import it.rate.webapp.models.Interest;
import it.rate.webapp.models.Role;
import it.rate.webapp.repositories.RoleRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManageInterestServiceTest extends BaseTest {

  @MockBean InterestService interestService;
  @MockBean RoleRepository roleRepository;
  @MockBean RoleService roleService;
  @MockBean UserService userService;

  @Autowired ManageInterestService manageInterestService;

  Interest i1;
  AppUser u1;

  @BeforeEach
  void setUp() {
    u1 = AppUser.builder().username("Lojza").id(1L).build();

    AppUser u2 = AppUser.builder().username("Alfonz").id(2L).build();

    AppUser u3 = AppUser.builder().username("Karel").id(3L).build();

    i1 = new Interest();

    Role r1 = new Role(u1, i1, Role.RoleType.CREATOR);
    Role r2 = new Role(u2, i1, Role.RoleType.VOTER);
    Role r3 = new Role(u3, i1, Role.RoleType.APPLICANT);

    i1.setRoles(List.of(r1, r2, r3));
  }



  @Test
  void inviteUserInvalidParameters() {
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(null, null, null, null));
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, null, null, null));
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(null, "email", null, null));
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(null, null, "franta", null));
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(null, null, null, Role.RoleType.APPLICANT));
    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, "email", null, null));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, null, "franta", null));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, null, null, Role.RoleType.APPLICANT));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, "email", "franta", null));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, "email", null, Role.RoleType.APPLICANT));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, null, "franta", Role.RoleType.APPLICANT));
  }

  @Test
  void inviteUserNonExistentInterestThrowsNotFound() {
    when(interestService.findById(any())).thenReturn(Optional.empty());
    Exception e =
        assertThrows(
            InvalidInterestDetailsException.class,
            () -> manageInterestService.inviteUser(1L, "username", "franta", Role.RoleType.VOTER));

    assertEquals("Interest with given details not found", e.getMessage());
  }

  @Test
  void createNewRoleNonExistentUser() {
    when(interestService.findById(any())).thenReturn(Optional.of(i1));
    when(userService.findByUsernameIgnoreCase(any())).thenReturn(Optional.empty());
    when(userService.findByUsernameIgnoreCase(any())).thenReturn(Optional.empty());

    assertThrows(
        InvalidUserDetailsException.class,
        () -> manageInterestService.inviteUser(1L, "username", "franta", Role.RoleType.APPLICANT));

    assertThrows(
        InvalidUserDetailsException.class,
        () ->
            manageInterestService.inviteUser(1L, "email", "test@test.cz", Role.RoleType.APPLICANT));
  }

  @Test
  void inviteUserReturnsRoleForValidParameters()
      throws InvalidInterestDetailsException, InvalidUserDetailsException {
    AppUser userWithoutRole = new AppUser();
    Role.RoleType roleToCreate = Role.RoleType.VOTER;
    when(interestService.findById(any())).thenReturn(Optional.of(i1));
    when(userService.findByUsernameIgnoreCase(any())).thenReturn(Optional.of(userWithoutRole));
    when(roleService.save(any())).then(i -> roleRepository.save(i.getArgument(0)));
    when(roleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Role newRole = manageInterestService.inviteUser(1L, "username", "franta", roleToCreate);

    verify(roleService, times(1)).save(newRole);
    verify(roleRepository, times(1)).save(newRole);
    assertEquals(roleToCreate, newRole.getRoleType());
    assertEquals(userWithoutRole, newRole.getAppUser());
  }

  @Test
  void inviteUserThrowsBadRequestForInvalidInviteForm() {
    when(interestService.findById(any())).thenReturn(Optional.of(i1));

    assertThrows(
        ConstraintViolationException.class,
        () -> manageInterestService.inviteUser(1L, "usernaaame", "franta", Role.RoleType.VOTER));

    assertThrows(
        ConstraintViolationException.class,
        () ->
            manageInterestService.inviteUser(
                1L, "emaail", "franta@franta.cz", Role.RoleType.VOTER));
  }
}
