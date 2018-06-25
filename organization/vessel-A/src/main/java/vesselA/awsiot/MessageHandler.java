package vesselA.awsiot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vesselA.controller.AwsClient;
import vesselA.repos.ShadowRepository;
import vesselA.domain.Destination;
import vesselA.domain.VesselShadow;
import vesselA.domain.VesselState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MessageHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AwsClient awsClient;

    @Autowired
    private ShadowRepository shadowRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    public void changeStatus(AWSIotMessage message) throws IOException {
        JsonNode rootNode = objectMapper.readTree(message.getStringPayload());
        String vid = rootNode.findValue("vid").asText();
        String status = rootNode.findValue("status").asText();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        String oldStatus = vesselShadow.getStatus();
        vesselShadow.setStatus(status);
        logger.info("status changed from"+oldStatus+ " to "+status);
    }


    public void vesselShadowForwarding(AWSIotMessage message) throws IOException {
        //admin 默认用户
        VesselShadow vesselShadow = null;
        JsonNode root = objectMapper.readTree(message.getStringPayload());
        String vid = root.get("state").get("desired").findValue("vid").asText();
        String simuStartTime = root.get("state").get("desired").findValue("simuStartTime").asText();
        int nextPortIndex = root.get("state").get("desired").findValue("nextPortIndex").asInt();
        int positionIndex = root.get("state").get("desired").findValue("positionIndex").asInt();
        String status = root.get("state").get("desired").findValue("status").asText();

        if (vid != null) {
            vesselShadow = shadowRepository.findById(vid);
            logger.info(positionIndex +" : "+vesselShadow.getPositionIndex());
            if (positionIndex >= vesselShadow.getPositionIndex()) { // if shadow received is new , update
                vesselShadow.setPositionIndex(positionIndex);
                //TODO : extract vessel state
                String vesselStateNode = root.get("state").get("desired").get("vesselState").toString();
                if(vesselStateNode.equals("null")){
                    vesselStateNode = null;
                }
                if (vesselStateNode != null) {
                    logger.debug(vesselStateNode);
                    VesselState vesselState = objectMapper.readValue(vesselStateNode, VesselState.class);
                    vesselShadow.updateVesselState(vesselState);
                }
                //TODO : extract vessel state
                JsonNode destinationsNode = root.get("state").get("desired").get("destinations");
                if(destinationsNode.asText().equals("null")){
                    destinationsNode = null;
                }
                if (destinationsNode!= null) {
                    List<Destination> destinations = new ArrayList<Destination>();
                    for(int i = 0 ; i < destinationsNode.size() ; i++){
                        Destination d = objectMapper.readValue(destinationsNode.get(i).toString() , Destination.class);
                        destinations.add(d);
                    }
                    vesselShadow.setDestinations(destinations);
                }
                //TODO : update some properties of vessel shadow.
                vesselShadow.setSimuStartTime(simuStartTime);
                vesselShadow.setNextPortIndex(nextPortIndex);
                vesselShadow.setStatus(status);
                vesselShadow.setPositionIndex(positionIndex);

                //TODO: publish vessel shadow to monitor
                ObjectNode payloadObjectNode = objectMapper.createObjectNode();
                switch (status){
                    case "Initiating" :
                        logger.info("Initiating is completed.");
                        awsClient.notifyVoyaging("IS_FIRST" , vid);
                        logger.debug("start voyaging");
                        break;
                    case "Voyaging"  :
                    case "Docking" :
                    case "Anchoring" :
                        logger.info(vesselShadow.getPositionIndex()+": "+vesselShadow.getStatus());
                        payloadObjectNode.putPOJO("vesselShadow" , vesselShadow);
                        //user should be set more flexibly
                        VesselShadow vd = shadowRepository.findById(vid);
                        logger.debug("send shadow to monitor : "+vd.toString());
                        simpMessagingTemplate.convertAndSendToUser( "admin","/topic/vesselShadow" , payloadObjectNode);
                        break;
                    default:
                        break;
                }
            }
        }

    }
}
