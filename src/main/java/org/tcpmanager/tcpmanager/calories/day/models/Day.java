package org.tcpmanager.tcpmanager.calories.day.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Date;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "day", schema = "calories")
public class Day {

  @Id
  @Column(nullable = false)
  private Date date;

  @Column(name = "user_id", nullable = false)
  private Long userId;
  @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<DayMeal> dayMeals;

}
