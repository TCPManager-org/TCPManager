package org.tcpmanager.tcpmanager.calories.ingredient;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<@NonNull Ingredient, @NonNull Long> {

}
