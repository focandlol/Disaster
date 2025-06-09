package focandlol.disaster.repository;

import focandlol.disaster.domain.es.ReportDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReportDocumentRepository extends ElasticsearchRepository<ReportDocument, String> {

}
