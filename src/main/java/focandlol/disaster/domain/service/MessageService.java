package focandlol.disaster.domain.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.FieldDateMath;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import focandlol.disaster.domain.es.MessageDocument;
import focandlol.disaster.dto.AggregationDto;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticsearchClient client;

  public List<AggregationDto> getSigunguAggregation(String from, String to, String sido) {
    Query query = NativeQuery.builder()
//        .withQuery(q -> q
//            .range(r -> r
//                .field("modifiedDate")
//                .gte(JsonData.of(from + "T00:00:00"))
//                .lte(JsonData.of(to + "T23:59:59"))
//            )
//        )
        .withAggregation("regions_nested",
            Aggregation.of(a -> a
                .nested(n -> n
                    .path("regions")
                )
                .aggregations("서울_시군구_필터",
                    Aggregation.of(f -> f
                        .filter(fl -> fl
                            .term(t -> t
                                .field("regions.sido.keyword")
                                .value(sido)
                            )
                        )
                        .aggregations("sigungu_agg",
                            Aggregation.of(t -> t
                                .terms(terms -> terms
                                    .field("regions.sigungu.keyword")
                                    .size(100)
                                    .order(
                                        Collections.singletonList(
                                            NamedValue.of("_count", SortOrder.Desc)))
                                )
                            )
                        )
                    )
                )
            )
        )
        .build();

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query,
        MessageDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) search.getAggregations();

    return aggregations.get("regions_nested")
        .aggregation()
        .getAggregate()
        .nested()
        .aggregations()
        .get("서울_시군구_필터")
        .filter()
        .aggregations()
        .get("sigungu_agg")
        .sterms()
        .buckets()
        .array()
        .stream()
        .map(b -> new AggregationDto(b.key().stringValue(), b.docCount()))
        .collect(Collectors.toList());


  }

  public List<AggregationDto> getSidoAggregation(String from, String to) {
    Query query = NativeQuery.builder()
        .withAggregation("regions_nested", Aggregation.of(a -> a
                .nested(n -> n.path("regions"))
                .aggregations("sido", Aggregation.of(ag -> ag
                        .terms(t -> t
                            .field("regions.sido.keyword")
                            .size(100))
                        .aggregations("reverse_sido", Aggregation.of(r -> r
                            .reverseNested(rn -> rn))
                        )
                    )
                )
            )
        ).build();

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query,
        MessageDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) search.getAggregations();

    return aggregations.get("regions_nested").aggregation()
        .getAggregate()
        .nested()
        .aggregations()
        .get("sido")
        .sterms()
        .buckets()
        .array()
        .stream()
        .map(
            bk -> new AggregationDto(bk.key().stringValue(),
                bk.aggregations().get("reverse_sido").reverseNested()
                    .docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getSido1Aggregation(String from, String to) throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-read")
            .aggregations("regions_nested", a -> a
                .nested(n -> n.path("regions"))
                .aggregations("sido_nested", s -> s
                    .terms(t -> t
                        .field("regions.sido.keyword")
                        .size(100))
                    .aggregations("reverse_sido", r -> r
                        .reverseNested(rn -> rn)))
            )

        , Void.class);

    return response.aggregations()
        .get("regions_nested")
        .nested()
        .aggregations()
        .get("sido_nested")
        .sterms()
        .buckets().array().stream()
        .map(b -> new AggregationDto(b.key().stringValue(),
            b.aggregations().get("reverse_sido").reverseNested().docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getCategory1Aggregation(String from, String to) throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-read")
            .query(q -> q
                .range(r -> r
                    .field("modifiedDate")
                    .gte(JsonData.of(from + "T00:00:00"))
                    .lte(JsonData.of(to + "T23:59:59"))
                )
            )
            .aggregations("카테고리_집계", t -> t
                .terms(term -> term
                    .field("category.keyword")
                    .size(100)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc))))
            )
        , Void.class);

    return response.aggregations().get("카테고리_집계")
        .sterms()
        .buckets()
        .array()
        .stream()
        .map(a -> new AggregationDto(a.key().stringValue(), a.docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getCategoryAggregation(String from, String to) {
    Query query = NativeQuery.builder()
        .withAggregation("category_agg", Aggregation.of(a -> a
                .terms(t -> t
                    .field("category.keyword")
                    .size(100)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            )
        ).build();

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query,
        MessageDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) search.getAggregations();

    return aggregations.get("category_agg").aggregation()
        .getAggregate()
        .sterms()
        .buckets()
        .array()
        .stream()
        .map(bk -> new AggregationDto(bk.key().stringValue(), bk.docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getYear1Aggregation(String year) throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-read")
            .size(0)
            .query(q -> q
                .range(r -> r
                    .field("createdAt")
                    .gte(JsonData.of(year + "-01-01"))
                    .lte(JsonData.of(year + "-12-31"))))
            .aggregations("달_집계", a -> a
                .dateHistogram(h -> h
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                    .minDocCount(1)
                    .extendedBounds(eb -> eb
                        .min(FieldDateMath.of(f -> f.expr(year + "-01")))
                        .max(FieldDateMath.of(f -> f.expr(year + "-12")))
                    )))
        , Void.class);

    return response.aggregations().get("달_집계")
        .dateHistogram()
        .buckets()
        .array()
        .stream()
        .map(m -> new AggregationDto(m.keyAsString(), m.docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getYearAggregation(String year) {
    Query query = NativeQuery.builder()
        .withAggregation("year_agg", Aggregation.of(a -> a
                .dateHistogram(h -> h
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                    .minDocCount(1)
                    .extendedBounds(eb -> eb
                        .min(FieldDateMath.of(f -> f.expr(year + "-01")))
                        .max(FieldDateMath.of(f -> f.expr(year + "-12")))
                    )
                )
            )
        ).build();

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query,
        MessageDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) search.getAggregations();

    return aggregations.get("year_agg").aggregation()
        .getAggregate()
        .dateHistogram()
        .buckets()
        .array()
        .stream()
        .map(bk -> new AggregationDto(bk.keyAsString(), bk.docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getMonth1Aggregation(String yearMonth) throws IOException {
    String minDate = yearMonth + "-01";
    String maxDate = LocalDate.parse(minDate).with(TemporalAdjusters.lastDayOfMonth()).toString();

    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-read")
            .size(0)
            .query(q -> q
                .range(r -> r
                    .field("createdAt")
                    .gte(JsonData.of(minDate))
                    .lte(JsonData.of(maxDate))))
            .aggregations("달_집계", a -> a
                .dateHistogram(h -> h
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Day)
                    .format("yyyy-MM-dd")
                    .minDocCount(1)
                    .extendedBounds(eb -> eb
                        .min(FieldDateMath.of(f -> f.expr(minDate)))
                        .max(FieldDateMath.of(f -> f.expr(maxDate)))
                    )))
        , Void.class);

    return response.aggregations().get("달_집계")
        .dateHistogram()
        .buckets()
        .array()
        .stream()
        .map(m -> new AggregationDto(m.keyAsString(), m.docCount()))
        .collect(Collectors.toList());
  }

  public List<AggregationDto> getMonthAggregation(String yearMonth) {
    String minDate = yearMonth + "-01";
    String maxDate = LocalDate.parse(minDate).with(TemporalAdjusters.lastDayOfMonth()).toString();

    Query query = NativeQuery.builder()
        .withQuery(q -> q
            .range(r -> r
                .field("createdAt")
                .gte(JsonData.of(minDate))
                .lte(JsonData.of(maxDate))))
        .withAggregation("month_agg", Aggregation.of(a -> a
                .dateHistogram(h -> h
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Month)
                    .minDocCount(0)
                    .extendedBounds(eb -> eb
                        .min(FieldDateMath.of(f -> f.expr(minDate)))
                        .max(FieldDateMath.of(f -> f.expr(maxDate)))
                    )
                )
            )
        ).build();

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query,
        MessageDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) search.getAggregations();

    return aggregations.get("month_agg")
        .aggregation().getAggregate()
        .dateHistogram()
        .buckets()
        .array()
        .stream()
        .map(bk -> new AggregationDto(bk.keyAsString(), bk.docCount()))
        .collect(Collectors.toList());
  }
}
