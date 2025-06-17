package focandlol.disaster;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import focandlol.disaster.service.SlackService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SlackAppender extends AppenderBase<ILoggingEvent> {

  private static SlackService slackService;

  public static void setSlackService(SlackService service) {
    slackService = service;
  }

  @Override
  protected void append(ILoggingEvent event) {
    if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
      slackService.sendErrorLog(event);
    }
  }
}
