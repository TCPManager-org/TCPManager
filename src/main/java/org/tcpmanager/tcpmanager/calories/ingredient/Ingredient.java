package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ingredients", schema = "calories")
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal calories;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal fats;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal carbs;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal proteins;

  @Column(nullable = false, length = 13)
  private String ean;
}
