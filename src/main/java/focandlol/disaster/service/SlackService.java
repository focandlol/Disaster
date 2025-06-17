package focandlol.disaster.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SlackService {

  @Value("${slack.webhook.url}")
  private String slackWebhookUrl;

  private final RestTemplate restTemplate = new RestTemplate();

  public void sendErrorLog(ILoggingEvent event) {
    String message = String.format(
        "*ERROR* in `%s.%s()`\n```\n%s\n```",
        event.getLoggerName(),
        event.getCallerData()[0].getMethodName(),
        event.getFormattedMessage()
    );

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", message), headers);
      restTemplate.postForEntity(slackWebhookUrl, request, String.class);
    } catch (Exception e) {
      log.error("슬랙 전송 실패", e);
    }
  }
}
