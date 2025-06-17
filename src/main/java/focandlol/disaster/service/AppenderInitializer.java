package focandlol.disaster.service;

import focandlol.disaster.SlackAppender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequiredArgsConstructor
public class AppenderInitializer implements InitializingBean {

  private final SlackService slackService;

  @Override
  public void afterPropertiesSet() throws Exception {
    SlackAppender.setSlackService(slackService);
  }
}
