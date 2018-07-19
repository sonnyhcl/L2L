package vesselA.awsiot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@SuppressWarnings("all")
public class ActivitiSubscriber extends AWSIotTopic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ObjectMapper objectMapper = new ObjectMapper();

    private MessageHandler messageHandler;

    public ActivitiSubscriber(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        String receivedTopic = message.getTopic();
        try {
            switch (receivedTopic) {
                case "$aws/things/vessel/shadow/update/accepted":
//                    logger.info("received message under topic : "+receivedTopic);
                    updateShadowHandler(message);
                    break;
                case "activiti/vessel/status/change" :
//                    logger.info("received message under topic : "+receivedTopic);
                    changeStatusHandler(message);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateShadowHandler(AWSIotMessage message) throws IOException {
        //TODO:update vessel shadow and forward shadow to Monitor
        this.messageHandler.vesselShadowForwarding(message);
    }

    public void changeStatusHandler(AWSIotMessage message) throws IOException {
        //TODO: report the signal of reaching port to process engine
        messageHandler.changeStatus(message);
    }




}