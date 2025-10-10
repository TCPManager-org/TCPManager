package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private BigDecimal fat;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal carbs;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal protein;

  @Column(nullable = false, length = 13)
  private String ean;
}
