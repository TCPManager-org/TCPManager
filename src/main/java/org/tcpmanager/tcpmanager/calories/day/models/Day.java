package org.tcpmanager.tcpmanager.calories.day.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Date;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.user.User;

@Entity
@Data
@NoArgsConstructor
@Table(name = "day", schema = "calories")
public class Day {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Date date;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "day", cascade = CascadeType.ALL)
  private Set<DayMeal> dayMeals;

}
