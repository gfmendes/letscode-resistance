package com.letscode.starwarresistance.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Data
public class Rebel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Integer age;

  private Integer genre; // TODO: Enum

  private boolean traitor;

  @OneToOne(mappedBy = "rebel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Location location;

  @Type(type = "json")
  @Column(columnDefinition = "json")
  private Map<String, Integer> inventory;

  @OneToMany(
      mappedBy = "traitorRebel",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<TreasonOccurrence> treasonOccurrences;

  public void addTreasonOccurrence(Long reporterId) {
    if (treasonOccurrences == null) {
      treasonOccurrences = new HashSet<>();
    }

    TreasonOccurrence treasonOccurrence = new TreasonOccurrence();
    treasonOccurrence.setTraitorRebel(this);
    treasonOccurrence.setReporterId(reporterId);
    treasonOccurrences.add(treasonOccurrence);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Rebel rebel = (Rebel) o;
    return Objects.equals(id, rebel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
