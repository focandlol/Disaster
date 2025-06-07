package focandlol.disaster.controller;

import focandlol.disaster.domain.service.MessageService;
import focandlol.disaster.dto.AggregationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @GetMapping("/sigungu_aggregation")
  public ResponseEntity<List<AggregationDto>> getAggregation(@RequestParam String from,
      @RequestParam String to, @RequestParam String sido) {
    return ResponseEntity.ok(messageService.getSigunguAggregation(from, to, sido));
  }

  @GetMapping("/sido_aggregation")
  public ResponseEntity<List<AggregationDto>> getSidoAggregation(@RequestParam String from,
      @RequestParam String to) {
    return ResponseEntity.ok(messageService.getSidoAggregation(from, to));
  }

  @GetMapping("/category_aggregation")
  public ResponseEntity<List<AggregationDto>> getCategoryAggregation(@RequestParam String from, @RequestParam String to) {
    return ResponseEntity.ok(messageService.getCategoryAggregation(from, to));

  }

}
