package focandlol.disaster.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.disaster.domain.Message;
import focandlol.disaster.repository.MessageRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DisasterMessageApiService {

  private final MessageRepository messageRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper mapper;

  @Value("${calamity.api.base-url}")
  private String baseUrl;

  @Value("${calamity.api.service-key}")
  private String serviceKey;

  public List<JsonNode> getDisasterMessageApi(){
    String startDate = getStartDate();

    List<JsonNode> jsonBatch = new ArrayList<>();

    int page = 1;
    int rows = 100;

    while(true){
      String json = fetchPage(page, rows, startDate, null);
      JsonNode data = extractItems(json);

      if(data == null || data.size() == 0) break;

      jsonBatch.add(data);

      page++;
    }

    return jsonBatch;
  }

  private String fetchPage(int pageNo, int numOfRows, String crtDt, String rgnNm) {
    try {
      StringBuilder url = new StringBuilder(baseUrl);
      url.append("?serviceKey=").append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
      url.append("&pageNo=").append(pageNo);
      url.append("&numOfRows=").append(numOfRows);
      url.append("&type=json");

      if (crtDt != null) {
        url.append("&crtDt=").append(crtDt);
      }
      if (rgnNm != null && !rgnNm.isBlank()) {
        url.append("&rgnNm=").append(URLEncoder.encode(rgnNm, StandardCharsets.UTF_8));
      }

      ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
      return response.getBody();
    } catch (Exception e) {
      throw new RuntimeException("API 호출 실패", e);
    }
  }

  private JsonNode extractItems(String json) {
    try {
      JsonNode root = mapper.readTree(json);
      return root.path("body");
    } catch (Exception e) {
      throw new RuntimeException("JSON 파싱 실패", e);
    }
  }

  private String getStartDate(){
    Message message = messageRepository.findTopByOrderByCreatedDateDesc()
        .orElseThrow(() -> new RuntimeException());

    LocalDateTime lastCreatedDate = message.getCreatedDate();

    if(lastCreatedDate == null) lastCreatedDate = LocalDate.parse("2025-01-01").atStartOfDay();

    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    return lastCreatedDate.format(outputFormatter);
  }

}
