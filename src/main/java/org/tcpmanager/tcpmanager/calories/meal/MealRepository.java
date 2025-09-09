package org.tcpmanager.tcpmanager.calories.meal;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<@NonNull Meal, @NonNull Long> {

}
