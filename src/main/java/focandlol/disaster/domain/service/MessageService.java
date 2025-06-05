package focandlol.disaster.domain.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import focandlol.disaster.domain.es.MessageDocument;
import focandlol.disaster.dto.AggregationDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final ElasticsearchOperations elasticsearchOperations;

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

    SearchHits<MessageDocument> search = elasticsearchOperations.search(query, MessageDocument.class);

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
}
