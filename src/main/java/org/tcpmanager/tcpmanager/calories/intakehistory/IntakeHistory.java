package org.tcpmanager.tcpmanager.calories.intakehistory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.user.User;

@Entity
@Data
@NoArgsConstructor
@Table(name = "intake_history", schema = "calories")
public class IntakeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Date date;

  @Column(nullable = false)
  private BigDecimal calories;

  @Column(nullable = false)
  private BigDecimal protein;

  @Column(nullable = false)
  private BigDecimal fat;

  @Column(nullable = false)
  private BigDecimal carbs;

  @Column(name = "calories_goal", nullable = false)
  private Integer caloriesGoal;

  @Column(name = "protein_goal", nullable = false)
  private Integer proteinGoal;

  @Column(name = "fat_goal", nullable = false)
  private Integer fatGoal;

  @Column(name = "carbs_goal", nullable = false)
  private Integer carbsGoal;

  @Column(nullable = false)
  private String username;
}
