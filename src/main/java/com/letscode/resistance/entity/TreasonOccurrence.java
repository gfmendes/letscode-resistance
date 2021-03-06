package com.letscode.resistance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"traitor_id", "reporterId"})})
@Data
public class TreasonOccurrence {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "traitor_id", nullable = false)
  @JsonIgnore
  private Rebel traitorRebel;

  private Long reporterId;
}
