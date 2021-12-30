package com.letscode.resistance.dto;

import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RebelDto {

  @NotNull private String name;
  @NotNull private Integer age;
  @NotNull private String genre;
  @NotNull private LocationDto location;
  @NotEmpty private Map<String, Integer> inventory;
}
