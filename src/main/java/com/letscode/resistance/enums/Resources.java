package com.letscode.resistance.enums;

import java.util.Arrays;
import java.util.List;

public enum Resources {
  WEAPON(4),
  AMMUNITION(3),
  WATER(2),
  FOOD(1);

  public final Integer weight;

  private Resources(Integer weight) {
    this.weight = weight;
  }

  public static List<String> getValidResources() {
    return Arrays.stream(Resources.values()).map(Resources::name).toList();
  }

  public static boolean isInvalidValidResource(String item) {
    return !getValidResources().contains(item.toUpperCase());
  }
}
