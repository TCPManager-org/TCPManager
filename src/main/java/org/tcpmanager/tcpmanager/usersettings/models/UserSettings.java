package org.tcpmanager.tcpmanager.usersettings.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.user.User;

@Entity
@Data
@NoArgsConstructor

@Table(name = "user_settings", schema = "public")
public class UserSettings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Gender gender;

  @Column(nullable = false)
  private Integer age;

  @Column(nullable = false)
  private Integer height;

  @Column(nullable = false)
  private BigDecimal weight;

  @Column(name = "body_fat_percentage", nullable = false)
  private BigDecimal bodyFatPercentage;

  @Enumerated(EnumType.STRING)
  @Column(name = "physical_activity", nullable = false, length = 20)
  private PhysicalActivity physicalActivity;

  @Column(name = "user_weight_goal", nullable = false)
  private BigDecimal userWeightGoal;

  @Column(name = "user_time_goal", nullable = false)
  private Date userTimeGoal;

  @Column(name = "calories_goal", nullable = false)
  private Integer caloriesGoal;

  @Column(name = "protein_goal", nullable = false)
  private Integer proteinGoal;

  @Column(name = "fat_goal", nullable = false)
  private Integer fatGoal;

  @Column(name = "carbs_goal", nullable = false)
  private Integer carbsGoal;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
