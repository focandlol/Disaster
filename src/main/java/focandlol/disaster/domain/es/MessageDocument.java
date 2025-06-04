package focandlol.disaster.domain.es;

import focandlol.disaster.domain.Message;
import jakarta.persistence.Id;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "message")
@Setting(settingPath = "/es/message-settings.json")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDocument {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;

  @Field(type = FieldType.Text, analyzer = "custom_text")
  private String text;

  @Field(type = FieldType.Text, analyzer = "custom_region")
  private String region;

  @Field(type = FieldType.Nested)
  private Set<Region> regions;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "custom_category"),
      otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword),
          @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "custom_category")})
  private String category;

  @Field(type = FieldType.Date)
  private String occurrenceDate;

  @Field(type = FieldType.Date)
  private String registeredDate;

  public static MessageDocument from(Message message,Set<Region> regions) {
    return MessageDocument.builder()
        .id(message.getSn())
        .text(message.getText())
        .region(message.getRegion())
        .regions(regions)
        .category(message.getCategory())
        .occurrenceDate(message.getOccurrenceDate().toString())
        .registeredDate(message.getCreatedDate().toString())
        .build();
  }

  private static String safe(String value) {
    if (value == null) {
      return null;
    }
    // 모든 공백 제거 (유니코드 포함)
    if (value.replaceAll("\\s+", "").isEmpty()) {
      return null;
    }
    return value;
  }

}
