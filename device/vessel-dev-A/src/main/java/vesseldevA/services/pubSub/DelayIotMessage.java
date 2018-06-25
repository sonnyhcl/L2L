package vesseldevA.services.pubSub;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vesseldevA.repos.CommonRepository;
import vesseldevA.repos.TrajectoryRepository;
import vesseldevA.services.shadow.MessageHandler;
import vesseldevA.services.shadow.VesselDevice;

import java.io.IOException;

public class DelayIotMessage extends AWSIotTopic {
    private  static Logger logger = Logger.getLogger(DelayIotMessage.class);
    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private TrajectoryRepository trajectoryRepository;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private VesselDevice vesselDevice;

    public DelayIotMessage(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onSuccess() {
        logger.debug("delay onSuccess");
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        //called when a message is received.
        String receivedTopic = message.getTopic();
        String vid = commonRepository.getVid();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootJNode = null;
        try {
            rootJNode = objectMapper.readTree(message.getStringPayload());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String msgType = rootJNode.findValue("msgType").asText();
        String from = rootJNode.findValue("from").asText();
        logger.debug("message :"+msgType+" from : "+from);
        if(from.equals(vid)){
            try {
                switch(receivedTopic) {
                    case "activiti/delay":
                        logger.info("received topic :"+message.getTopic());
                        delayHandler(rootJNode);
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void delayHandler(JsonNode rootNode) throws IOException {
        messageHandler.delay(rootNode);

    }
}
