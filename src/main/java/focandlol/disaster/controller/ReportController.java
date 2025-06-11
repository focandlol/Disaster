package focandlol.disaster.controller;

import focandlol.disaster.dto.CreateReportDto;
import focandlol.disaster.dto.ReportDto;
import focandlol.disaster.dto.UpdateReportDto;
import focandlol.disaster.service.ReportService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
  private final ReportService reportService;

  @GetMapping("{reportId}")
  public ResponseEntity<ReportDto> getReport(@PathVariable Long reportId) {
    return ResponseEntity.ok(reportService.getReport(reportId));
  }

  @PostMapping
  public ResponseEntity<Void> createReport(@RequestBody CreateReportDto reportDto) {
    Long id = reportService.create(reportDto);
    URI uri = URI.create("/report/" + id);
    return ResponseEntity.created(uri).build();
  }

  @PutMapping("/{reportId}")
  public ResponseEntity<Void> updateReport(@PathVariable Long reportId,
      @RequestBody UpdateReportDto reportDto) {
    reportService.update(reportId, reportDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{reportId}")
  public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
    reportService.delete(reportId);
    return ResponseEntity.noContent().build();
  }
}
