package org.tcpmanager.calories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.calories.models.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
