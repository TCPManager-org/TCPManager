package org.tcpmanager.tcpmanager.calories.ingredient;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<@NonNull Ingredient, @NonNull Long> {
  Optional<Ingredient> findByEan(String ean);
}
