package focandlol.disaster.repository;

import focandlol.disaster.domain.Message;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void batchInsertIgnore(List<Message> messages) {
    if (messages == null || messages.isEmpty()) {
      return;
    }

    String sql = "INSERT IGNORE INTO message (sn, text, category, region, occurrence_date, created_date) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        Message message = messages.get(i);
        ps.setString(1, message.getSn());
        ps.setString(2, message.getText());
        ps.setString(3, message.getCategory());
        ps.setString(4, message.getRegion());

        if (message.getOccurrenceDate() != null) {
          ps.setTimestamp(5, Timestamp.valueOf(message.getOccurrenceDate()));
        } else {
          ps.setNull(5, Types.TIMESTAMP);
        }

        if (message.getCreatedDate() != null) {
          ps.setTimestamp(6, Timestamp.valueOf(message.getCreatedDate()));
        } else {
          ps.setNull(6, Types.TIMESTAMP);
        }
      }

      @Override
      public int getBatchSize() {
        return messages.size();
      }
    });
  }
}
