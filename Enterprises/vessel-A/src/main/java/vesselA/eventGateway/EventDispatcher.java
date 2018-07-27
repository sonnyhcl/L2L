package vesselA.eventGateway;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SuppressWarnings("all")
public class EventDispatcher extends AWSIotTopic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ObjectMapper objectMapper = new ObjectMapper();

    private EventHandler eventHandler;

    public EventDispatcher(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    public  void setEventHandler(EventHandler eventHandler){
        this.eventHandler = eventHandler;
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        String receivedTopic = message.getTopic();
        try {
            if(receivedTopic.equals("$aws/things/V413362260/shadow/update/accepted")){
                updateShadowHandler(message);
            }
            if(receivedTopic.equals("IoT/V413362260/status")){
                changeStatusHandler(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateShadowHandler(AWSIotMessage message) throws IOException {
        //TODO:update vessel shadow and forward shadow to Monitor
        this.eventHandler.vesselShadowForwarding(message);
    }

    public void changeStatusHandler(AWSIotMessage message) throws IOException {
        //TODO: report the signal of reaching port to process engine
        this.eventHandler.changeStatus(message);
    }




}