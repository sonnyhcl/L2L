package iot.service.pubSub;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import iot.domain.Path;
import iot.repos.CommonRepository;
import iot.repos.PathRepository;
import iot.service.shadow.MessageHandler;
import iot.service.shadow.WagonDevice;

import java.io.IOException;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 * Author : bqzhu
 */
public class WagonSubscriber extends AWSIotTopic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private WagonDevice wagonDevice;

    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private PathRepository pathRepository;

    public WagonSubscriber(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        //called when a message is received.
        String receivedTopic = message.getTopic();
        String wid = commonRepository.getWid();
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
        if(from.equals(wid)){
            try {
                switch(receivedTopic) {
                    case "activiti/wagon/navigate" :
                        logger.info("received topic :"+message.getTopic());
                        Path path = objectMapper.readValue(rootJNode.get("msgBody").asText() , Path.class);
                        pathRepository.save(path);
                        messageHandler.reportWagonState();
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
