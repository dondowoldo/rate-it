package it.rate.webapp.services;

import it.rate.webapp.models.Category;
import it.rate.webapp.repositories.CategoryRepository;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

@Service
@Validated
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final int MAX_CATEGORIES_PER_INTEREST = 3;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public int getMaxCategories() {
    return this.MAX_CATEGORIES_PER_INTEREST;
  }

  public List<Category> findAllByIdIn(Set<Long> ids) {
    return categoryRepository.findAllByIdIn(ids);
  }

  public List<Category> findMaxLimitByIdIn(@Size(max = MAX_CATEGORIES_PER_INTEREST) Set<Long> ids) {
    return findAllByIdIn(ids);
  }
}
