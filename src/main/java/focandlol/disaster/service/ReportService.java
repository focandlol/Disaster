package focandlol.disaster.service;

import focandlol.disaster.domain.Report;
import focandlol.disaster.dto.CreateReportDto;
import focandlol.disaster.dto.ReportDto;
import focandlol.disaster.dto.UpdateReportDto;
import focandlol.disaster.repository.ReportDocumentRepository;
import focandlol.disaster.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportRepository reportRepository;

  public Long create(CreateReportDto reportDto) {
    Report report = reportRepository.save(reportDto.to());

    return report.getId();
  }

  @Transactional
  public void update(Long reportId, UpdateReportDto reportDto) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new RuntimeException("can't find report with id: " + reportId));

    report.setCategory(reportDto.getCategory());
    report.setTitle(reportDto.getTitle());
    report.setContent(reportDto.getContent());
    report.setJibunAddress(reportDto.getJibunAddress());
    report.setRoadAddress(reportDto.getRoadAddress());
  }


  public void delete(Long reportId) {
    reportRepository.deleteById(reportId);
  }

  public ReportDto getReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new RuntimeException("can't find report with id: " + reportId));

    return ReportDto.from(report, report.getMember().getEmail());
  }
}
