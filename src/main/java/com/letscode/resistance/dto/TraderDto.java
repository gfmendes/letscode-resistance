package com.letscode.resistance.dto;

import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TraderDto {

  @NotNull private Long rebelId;
  @NotNull private Map<String, Integer> items;
}
