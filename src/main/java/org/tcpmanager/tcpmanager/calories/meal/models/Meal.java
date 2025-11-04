package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "meals", schema = "calories")
public class Meal {

  @Column(nullable = false)
  Boolean favorite;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<MealIngredient> mealIngredients;
}
