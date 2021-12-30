package com.letscode.resistance.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReporterDto {

  @NotNull private Long reporterId;
}
