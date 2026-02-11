package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.*;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tcpmanager.tcpmanager.user.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "meals", schema = "calories")
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
  private Set<MealIngredient> mealIngredients;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
