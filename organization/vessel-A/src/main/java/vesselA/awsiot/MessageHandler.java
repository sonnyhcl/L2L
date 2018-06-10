package vesselA.awsiot;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import vesselA.repos.VesselCache;
import vesselA.util.SpringUtil;
import vesselA.vessel.domain.Port;
import vesselA.vessel.domain.VesselShadow;
import vesselA.vessel.domain.VesselState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MessageHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskService taskService;

    @Autowired
    private VesselCache vesselCache;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    public void changeStatus(AWSIotMessage message) throws IOException {
        JsonNode rootNode = objectMapper.readTree(message.getStringPayload());
        String vid = rootNode.findValue("vid").asText();
        String status = rootNode.findValue("status").asText();
        String oldStatus = vesselCache.getVesselShadows().get(vid).getStatus();
        vesselCache.getVesselShadows().get(vid).setStatus(status);
        logger.info("status changed from"+oldStatus+ " to "+status);
    }


    public void vesselShadowForwarding(AWSIotMessage message) throws IOException {
        //admin 默认用户
        VesselCache vesselCache = (VesselCache) SpringUtil.getBean("vesselCache");
        VesselShadow vesselShadow = null;
        JsonNode root = objectMapper.readTree(message.getStringPayload());
        JsonNode vesselStateJson = root.get("state").get("desired").get("vesselState");
        String vid = root.get("state").get("desired").findValue("vid").asText();
        String simuStartDateStr = root.get("state").get("desired").findValue("simuStartDateStr").asText();
        int nextPortIndex = root.get("state").get("desired").findValue("nextPortIndex").asInt();
        int positionIndex = root.get("state").get("desired").findValue("positionIndex").asInt();
        String status = root.get("state").get("desired").findValue("status").asText();

        if (vid != null) {
            vesselShadow = vesselCache.getVesselShadows().get(vid);
            logger.info(positionIndex +" : "+vesselShadow.getPositionIndex());
            if (vesselShadow == null) { // first time to create vesselShadow
                vesselShadow = new VesselShadow();
                vesselShadow.setVid(vid);
            }
            if (positionIndex >= vesselShadow.getPositionIndex()) { // if shadow received is new , update
                vesselShadow.setPositionIndex(positionIndex);
                if (vesselStateJson != null) {
                    String vesselStateJsonStr = vesselStateJson.toString();
                    if(!(("null").equals(vesselStateJsonStr))){
                        logger.debug(vesselStateJsonStr);
                        VesselState vesselState = objectMapper.readValue(vesselStateJsonStr, VesselState.class);
                        vesselShadow.setVesselState(vesselState);
                    }
                }
                JsonNode portsJNode = root.get("state").get("desired").get("ports");
                if (portsJNode!= null) {
                    List<Port> ports = new ArrayList<Port>();
                    for(int i = 0 ; i < portsJNode.size() ; i++){
                        Port p = objectMapper.readValue(portsJNode.get(i).toString() , Port.class);
                        ports.add(p);
                    }
                    vesselShadow.setPorts(ports);
                }
                vesselShadow.setSimuStartDateStr(simuStartDateStr);
                vesselShadow.setNextPortIndex(nextPortIndex);
                vesselShadow.setStatus(status);
                vesselShadow.setPositionIndex(positionIndex);
                //update vessel cache
                vesselCache.getVesselShadows().put(vid , vesselShadow);
                //publish vessel shadow to monitor
                ObjectNode payloadObjectNode = objectMapper.createObjectNode();
                switch (status){
                    case "Initiating" :
                        logger.info("Initiating is completed.");
                        payloadObjectNode.put("msgType" , "IS_FIRST");
                        payloadObjectNode.put("from" , vid);
                        logger.debug("payload :"+payloadObjectNode.toString());
                        try {
                            AWSIotMqttClient awsIotMqttClient = (AWSIotMqttClient)SpringUtil.getBean("awsIotMqttClient");
                            AWSIotMessage voyaPub = new MessagePublisher("activiti/vessel/voyaging", AWSIotQos.QOS0, payloadObjectNode.toString());
                            awsIotMqttClient.publish(voyaPub);
                        } catch (AWSIotException e) {
                            e.printStackTrace();
                        }
                        logger.debug("start voyaging");
                        break;
                    case "Voyaging"  :
                    case "Docking" :
                    case "Anchoring" :
                        logger.info(vesselShadow.getPositionIndex()+": "+vesselShadow.getStatus());
                        payloadObjectNode.putPOJO("vesselShadow" , vesselShadow);
                        //user should be set more flexibly
                        VesselShadow vd = vesselCache.getVesselShadows().get(vid);
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
