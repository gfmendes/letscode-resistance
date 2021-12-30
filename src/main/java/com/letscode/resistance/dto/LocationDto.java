package com.letscode.resistance.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationDto {

  @NotNull private String name;
  @NotNull private Long latitude;
  @NotNull private Long longitude;
}
