package focandlol.disaster;

import com.fasterxml.jackson.databind.JsonNode;
import focandlol.disaster.domain.Message;
import focandlol.disaster.domain.es.MessageDocument;
import focandlol.disaster.domain.es.Region;
import focandlol.disaster.service.DisasterMessageApiService;
import focandlol.disaster.service.MessageUtil;
import focandlol.disaster.repository.MessageDocumentRepository;
import focandlol.disaster.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageScheduler {

  private final DisasterMessageApiService disasterMessageApiService;
  private final MessageUtil messageUtil;
  private final MessageRepository messageRepository;
  private final MessageDocumentRepository messageDocumentRepository;

  //@Scheduled(fixedRate = 120000)
  public void messageSchedule(){
    List<JsonNode> disasterMessageApi = disasterMessageApiService.getDisasterMessageApi();

    List<Message> messageBatch = new ArrayList<>();
    List<MessageDocument> messageDocumentBatch = new ArrayList<>();

    for(JsonNode jsonNode : disasterMessageApi){
      for(JsonNode messages : jsonNode){

        Set<Region> regionSet = messageUtil.getRegionSet(messages.path("RCPTN_RGN_NM").asText().trim());

        Message message = Message.from(messages);

        MessageDocument messageDocument = MessageDocument.from(message, regionSet);

        messageBatch.add(message);
        messageDocumentBatch.add(messageDocument);

        log.info(message.getCategory());
      }
    }
    System.out.println("message size" + messageBatch.size());
    System.out.println("message batch size" + messageBatch.size());

    messageRepository.batchInsertIgnore(messageBatch);
    messageDocumentRepository.saveAll(messageDocumentBatch);

    log.info("scheduler 종료");
  }

}
