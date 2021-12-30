package com.letscode.starwarresistance.dto;

import java.util.Map;
import lombok.Data;

@Data
public class TraderDto {

  private Long rebelId;
  private Map<String, Integer> items;
}
