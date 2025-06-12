package focandlol.disaster.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import focandlol.disaster.domain.Report;
import focandlol.disaster.domain.es.ReportDocument;
import focandlol.disaster.dto.CreateReportDto;
import focandlol.disaster.dto.ReportDto;
import focandlol.disaster.dto.ReportListDto;
import focandlol.disaster.dto.ReportSearchDto;
import focandlol.disaster.dto.UpdateReportDto;
import focandlol.disaster.repository.ReportDocumentRepository;
import focandlol.disaster.repository.ReportRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportRepository reportRepository;
  private final ReportDocumentRepository reportDocumentRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  @Transactional
  public Long create(CreateReportDto reportDto) {
    Report report = reportRepository.save(reportDto.to());

    reportDocumentRepository.save(ReportDocument.from(report));

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

    reportDocumentRepository.save(ReportDocument.from(report));
  }

  @Transactional
  public void delete(Long reportId) {
    reportRepository.deleteById(reportId);

    reportDocumentRepository.deleteById(reportId.toString());
  }

  public ReportDto getReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new RuntimeException("can't find report with id: " + reportId));

    return ReportDto.from(report, report.getMember().getEmail());
  }

  public List<String> titleAuto(String query){
    NativeQuery nativeQuery = NativeQuery.builder()
        .withQuery(MultiMatchQuery.of(m -> m
            .query(query)
            .type(TextQueryType.BoolPrefix)
            .fields("title.auto_complete", "title.auto_complete._2gram",
                "title.auto_complete._3gram", "title.auto_complete._index_prefix"))._toQuery())
        .withPageable(PageRequest.of(0, 5))
        .build();

    SearchHits<ReportDocument> search = elasticsearchOperations.search(nativeQuery,
        ReportDocument.class);

    return search.getSearchHits().stream()
        .map(hit -> hit.getContent().getTitle())
        .collect(Collectors.toList());
  }

  public Page<ReportListDto> getReports(ReportSearchDto searchDto, Pageable pageable) {
    List<Query> must = new ArrayList<>();

    if (searchDto.getQuery() != null && !searchDto.getQuery().isEmpty()) {
      Query multiMatchQuery = MultiMatchQuery.of(m -> m
          .query(searchDto.getQuery())
          .fields("title^3", "content", "category^2")
          .fuzziness("AUTO")
      )._toQuery();
      must.add(multiMatchQuery);
    }

    if (searchDto.getCategory() != null && !searchDto.getCategory().isEmpty()) {
      must.add(MatchQuery.of(q -> q
          .query(searchDto.getCategory())
          .field("category")
      )._toQuery());
    }

    if (searchDto.getJibunAddress() != null && !searchDto.getJibunAddress().isEmpty()) {
      must.add(MatchQuery.of(q -> q
          .query(searchDto.getJibunAddress())
          .field("jibunAddress")
      )._toQuery());
    }

    if (searchDto.getRoadAddress() != null && !searchDto.getRoadAddress().isEmpty()) {
      must.add(MatchQuery.of(q -> q
          .query(searchDto.getRoadAddress())
          .field("roadAddress")
      )._toQuery());
    }

//    Query dateRange = RangeQuery.of(r -> r
//        .field("createdAt")
//        .gte(JsonData.of(searchDto.getStartDate()))
//        .lte(JsonData.of(searchDto.getEndDate()))
//    )._toQuery();

    Query boolQuery = BoolQuery.of(b -> b
            .must(must)
        //.filter(dateRange)
    )._toQuery();

    NativeQuery nativeQuery = NativeQuery.builder()
        .withQuery(boolQuery)
        .withPageable(PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize()))
        .build();

    SearchHits<ReportDocument> search = elasticsearchOperations.search(nativeQuery,
        ReportDocument.class);

    List<ReportListDto> collect = search.getSearchHits().stream()
        .map(hit -> ReportListDto.from(hit.getContent()))
        .collect(Collectors.toList());

    return new PageImpl<>(collect,
        PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize()),
        search.getTotalHits());
  }
}
