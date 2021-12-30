package com.letscode.starwarresistance.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TradeRequestDto {

  @NotEmpty
  private List<TraderDto> traders;
}
