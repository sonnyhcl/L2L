package vesselA.coordinator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vesselA.domain.Destination;
import vesselA.domain.VesselShadow;
import vesselA.repos.ShadowRepository;

import java.util.List;

@Data
@Service
public class StompClient {
    private static final Logger logger = LoggerFactory.getLogger(StompClient.class);

//    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShadowRepository shadowRepository;

    public StompClient( SimpMessagingTemplate  simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendVesselShadow(String username, String topic, VesselShadow vesselShadow) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putPOJO("vesselShadow", vesselShadow);
        simpMessagingTemplate.convertAndSendToUser(username, topic, payload);
    }

    public void sendValidPorts(String username, String topic, List<Destination> destinationList) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.putPOJO("destinations", destinationList);
        simpMessagingTemplate.convertAndSendToUser(username, topic, payload);
    }

    public void sendCurrentPort(String username, String topic, Destination cur, String status ,  Destination next) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload = objectMapper.createObjectNode();
        payload.putPOJO("port", cur);
        payload.putPOJO("status", status);
        payload.putPOJO("nextPort", next);
        simpMessagingTemplate.convertAndSendToUser(username, topic, payload);
    }

    public void sendMissingMsg (String username, String topic, String pid  , String  msgType ){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("pid" , pid);
        payload.put("msgType" , msgType);
        simpMessagingTemplate.convertAndSendToUser( username, topic , payload);
    }

    public void sendMeetMsg (String username, String topic, String pid  , String  msgType , String rend ){
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("pid" , pid);
        payload.put("msgType" , msgType);
        payload.put("rendezvous" , rend);
        simpMessagingTemplate.convertAndSendToUser( username, topic , payload);
    }


}
