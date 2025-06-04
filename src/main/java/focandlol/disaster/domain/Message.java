package focandlol.disaster.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String sn;

  private String text;

  private String category;

  private String region;

  private LocalDateTime occurrenceDate;

  private LocalDateTime createdDate;

  public static Message from(JsonNode node) {
    return Message.builder()
        .sn(node.path("SN").asText())
        .text(node.path("MSG_CN").asText())
        .region(node.path("RCPTN_RGN_NM").asText().trim())
        .category(node.path("DST_SE_NM").asText())
        .occurrenceDate(format(node.path("CRT_DT").asText()))
        .createdDate(format(node.path("REG_YMD").asText()))
        .build();
  }

  public static LocalDateTime format(String date){
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy/MM/dd HH:mm:ss")  // 초까지
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .toFormatter();

    return LocalDateTime.parse(date, formatter);
  }
}
