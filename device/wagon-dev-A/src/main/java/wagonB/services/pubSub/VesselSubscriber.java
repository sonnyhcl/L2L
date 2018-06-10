package wagonB.services.pubSub;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import wagonB.conf.VesselStateCache;
import wagonB.services.shadow.MessageHandler;
import wagonB.services.shadow.VesselDevice;
import wagonB.util.SpringUtil;

import java.io.IOException;

/**
 * This class extends {@link AWSIotTopic} to receive messages from a subscribed
 * topic.
 * Author : bqzhu
 */
public class VesselSubscriber extends AWSIotTopic {
    private  static Logger logger = Logger.getLogger(VesselSubscriber.class);

    public VesselSubscriber(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        //called when a message is received.
        String receivedTopic = message.getTopic();
//        logger.debug(receivedTopic);
        VesselStateCache vesselStateCache = (VesselStateCache) SpringUtil.getBean("vesselStateCache");
        MessageHandler messageHandler = (MessageHandler)SpringUtil.getBean("messageHandler");
        String vid = vesselStateCache.getVid();
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
        VesselDevice vesselDevice = (VesselDevice)SpringUtil.getBean("vesselDevice");
        if(from.equals(vid)){
            try {
                switch(receivedTopic) {
                    case "activiti/vessel/init" :
                        logger.info("received topic :"+message.getTopic());
                        initHandler(rootJNode , messageHandler);
                        break;
                    case "activiti/vessel/voyaging":
                        logger.info("received topic :"+message.getTopic());
                        voyagingHandler(rootJNode , messageHandler);
                        break;
                    case "activit/vessel/delay":
                        logger.info("received topic :"+message.getTopic());
                        delayHandler(rootJNode , messageHandler);
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (AWSIotException e) {
                e.printStackTrace();
            }

        }

    }

    public void voyagingHandler(JsonNode rootNode , MessageHandler messageHandler) throws IOException, InterruptedException, AWSIotException {
        messageHandler.reportVesselState(rootNode);
    }

    public void initHandler(JsonNode rootNode , MessageHandler messageHandler) throws IOException, InterruptedException, AWSIotException {
        messageHandler.initVesselState(rootNode);
    }

    public void delayHandler(JsonNode rootNode, MessageHandler messageHandler) throws IOException {
        messageHandler.delay(rootNode);

    }
}
