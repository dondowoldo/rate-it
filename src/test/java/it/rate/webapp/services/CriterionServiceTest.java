package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.models.Criterion;
import it.rate.webapp.models.Interest;
import it.rate.webapp.repositories.CriterionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CriterionServiceTest extends BaseTest {
  @MockBean CriterionRepository criterionRepository;

  @Autowired CriterionService criterionService;

  @Test
  void createNew() {}

  @Test
  void updateExistingAddCriteria() {
    Criterion cr1 = Criterion.builder().name("Křupavost").build();
    Criterion cr2 = Criterion.builder().name("Kvalita").build();
    List<Criterion> criteria = List.of(cr1, cr2);
    Interest interest =
        Interest.builder().id(1L).name("test").description("desc").criteria(criteria).build();

    Set<String> newCriteriaNames = Set.of("Křupavost", "Kvalita", "Velikost", "Užitek");

    when(criterionRepository.findAllByInterest(any())).thenReturn(criteria);

    criterionService.updateExisting(interest, newCriteriaNames);

    verify(criterionRepository, times(2)).save(any());
    verify(criterionRepository, times(0)).delete(any());
  }

  @Test
  void updateExistingDeleteCriterion() {
    Criterion cr1 = Criterion.builder().name("Křupavost").build();
    Criterion cr2 = Criterion.builder().name("Kvalita").build();
    Criterion cr3 = Criterion.builder().name("Velikost").build();
    List<Criterion> criteria = List.of(cr1, cr2, cr3);
    Interest interest =
        Interest.builder().id(1L).name("test").description("desc").criteria(criteria).build();

    Set<String> newCriteriaNames = Set.of("Křupavost", "Kvalita");

    when(criterionRepository.findAllByInterest(any())).thenReturn(criteria);

    criterionService.updateExisting(interest, newCriteriaNames);

    verify(criterionRepository, times(0)).save(any());
    verify(criterionRepository, times(1)).delete(any());
  }

  @Test
  void updateExistingDeleteAndAddCriteria() {
    Criterion cr1 = Criterion.builder().name("Křupavost").build();
    Criterion cr2 = Criterion.builder().name("Kvalita").build();
    Criterion cr3 = Criterion.builder().name("Velikost").build();
    List<Criterion> criteria = List.of(cr1, cr2, cr3);
    Interest interest =
        Interest.builder().id(1L).name("test").description("desc").criteria(criteria).build();

    Set<String> newCriteriaNames = Set.of("Křupavost", "Kvalita", "Užitek");

    when(criterionRepository.findAllByInterest(any())).thenReturn(criteria);

    criterionService.updateExisting(interest, newCriteriaNames);

    verify(criterionRepository, times(1)).save(any());
    verify(criterionRepository, times(1)).delete(any());
  }
}
