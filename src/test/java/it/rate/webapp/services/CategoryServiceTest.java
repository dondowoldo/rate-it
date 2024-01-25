package it.rate.webapp.services;

import it.rate.webapp.BaseTest;
import it.rate.webapp.models.Category;
import it.rate.webapp.repositories.CategoryRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoryServiceTest extends BaseTest {
  @MockBean CategoryRepository categoryRepository;
  @Autowired CategoryService categoryService;

  @Test
  void findMaxLimitByIdIn_TooManyCategoriesThrowsException() {
    when(categoryRepository.findAllByIdIn(any()))
        .thenReturn(
            List.of(new Category("Test 1"), new Category("Test 2"), new Category("Test 3")));
    Set<Long> categoryIds = Set.of(1L, 2L, 3L, 4L);

    assertThrows(
        ConstraintViolationException.class, () -> categoryService.findMaxLimitByIdIn(categoryIds));
  }
}
