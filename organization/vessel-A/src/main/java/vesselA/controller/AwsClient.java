package vesselA.controller;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vesselA.awsiot.MessagePublisher;
import vesselA.domain.Destination;

import java.util.List;

@Service
public class AwsClient {
    private static  final Logger logger = LoggerFactory.getLogger(AwsClient.class);
    private static final String delayTopic = "activiti/delay";
    private static final String initTopic = "activiti/vessel/init";
    private static final String voyagingTopic = "activiti/vessel/voyaging";

    @Autowired
    private AWSIotMqttClient awsIotMqttClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * delay or postpone
     * @param msgType
     * @param from
     * @param destinations
     */
    public void sendDestinations(String msgType , String from , List<Destination> destinations) throws JsonProcessingException {
        String payload = null;
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("msgType", msgType);
        payloadObjectNode.put("from", from);
        payloadObjectNode.putPOJO("destinations", objectMapper.writeValueAsString(destinations));
        payload = payloadObjectNode.toString();
        logger.debug(payload);
        AWSIotMessage pub = new MessagePublisher(delayTopic, AWSIotQos.QOS0, payloadObjectNode.toString());
        try {
            awsIotMqttClient.publish(pub);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
        logger.debug("destinations is sent");
    }


    public  void sendInitiation(String msgTyoe , String from , int defaultDelayHour , int zoomInVal){
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("msgType" , msgTyoe);
        payloadObjectNode.put("from" , from);
        payloadObjectNode.put("defaultDelayHour" , defaultDelayHour);
        payloadObjectNode.put("zoomInVal" ,  zoomInVal);
        logger.debug("payload :"+payloadObjectNode.toString());
        AWSIotMessage pub = new MessagePublisher(initTopic, AWSIotQos.QOS0, payloadObjectNode.toString());
        try {
            awsIotMqttClient.publish(pub);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

    public void notifyVoyaging(String msgTyoe , String from ){
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("msgType", msgTyoe);
        payloadObjectNode.put("from", from);
        logger.debug("payload :" + payloadObjectNode.toString());
        AWSIotMessage pub = new MessagePublisher(voyagingTopic, AWSIotQos.QOS0, payloadObjectNode.toString());
        try {
            awsIotMqttClient.publish(pub);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

}
