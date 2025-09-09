package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.calories.meal_ingredient.MealIngredient;

@Entity
@Data
@NoArgsConstructor
@Table(name = "meals", schema = "calories")
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
  private List<MealIngredient> ingredients = new ArrayList<>();
}
