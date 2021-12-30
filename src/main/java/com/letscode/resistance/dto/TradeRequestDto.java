package com.letscode.resistance.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class TradeRequestDto {

  @NotEmpty
  @Size(min = 2, max = 2)
  private List<TraderDto> traders;
}
