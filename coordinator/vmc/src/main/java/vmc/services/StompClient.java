package vmc.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vmc.domain.ManagerPart;
import vmc.services.RestClient;

@Service
@SuppressWarnings("all")
public class StompClient {
    private static  final Logger logger = LoggerFactory.getLogger(StompClient.class);

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RestClient restClient;
    @Autowired
    private ObjectMapper objectMapper;

    public void showManager(ManagerPart managerPart){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putPOJO("location" , restClient.getLoc(managerPart));
        simpMessagingTemplate.convertAndSend("/topic/destinations/invalid", payload);
        logger.debug("send manager location "+managerPart.getLocation());
    }

}
