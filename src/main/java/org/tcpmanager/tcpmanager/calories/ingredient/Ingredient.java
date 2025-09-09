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

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal calories;

  @Column(nullable = false)
  private BigDecimal fat;

  @Column(nullable = false)
  private BigDecimal carbs;

  @Column(nullable = false)
  private BigDecimal protein;

  @Column(nullable = false, length = 13)
  private String ean;
}
