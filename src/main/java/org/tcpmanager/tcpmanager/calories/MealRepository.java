package org.tcpmanager.tcpmanager.calories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.models.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
