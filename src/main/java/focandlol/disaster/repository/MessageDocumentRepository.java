package focandlol.disaster.repository;

import focandlol.disaster.domain.es.MessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, String> {

}
