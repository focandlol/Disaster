package focandlol.disaster.controller;

import focandlol.disaster.domain.service.MessageService;
import focandlol.disaster.dto.AggregationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @GetMapping("/sigungu_aggregation")
  public List<AggregationDto> getAggregation(@RequestParam String from,
      @RequestParam String to, @RequestParam String sido) {
    return messageService.getSigunguAggregation(from, to, sido);
  }

}
