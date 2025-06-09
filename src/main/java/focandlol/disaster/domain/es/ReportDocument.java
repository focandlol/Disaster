package focandlol.disaster.domain.es;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "report")
@Setting(settingPath = "/es/report-settings.json")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDocument {

  @Id
  private String id;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "report_title_analyzer"),
      otherFields = {
          @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori"),
      }
  )
  private String title;

  @Field(type = FieldType.Text, analyzer = "report_content_analyzer")
  private String content;

  @Field(type = FieldType.Text, analyzer = "report_address_analyzer")
  private String roadAddress;

  @Field(type = FieldType.Text, analyzer = "report_address_analyzer")
  private String jibunAddress;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "report_category_analyzer"),
      otherFields = {
          @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
      }
  )
  private String category;

  @Field(type = FieldType.Keyword)
  private String email;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime updatedAt;

}
