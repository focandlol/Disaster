package focandlol.disaster.repository;

import focandlol.disaster.domain.Message;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {

  Optional<Message> findTopByOrderByCreatedDateDesc();

  boolean existsBySn(String sn);

}
