package focandlol.disaster.repository;

import focandlol.disaster.domain.Message;
import java.util.List;

public interface MessageRepositoryCustom {
  void batchInsertIgnore(List<Message> messages);
}
