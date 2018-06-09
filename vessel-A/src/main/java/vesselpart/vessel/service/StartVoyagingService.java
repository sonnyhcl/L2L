package vesselpart.vessel.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vesselpart.awsiot.MessagePublisher;
import vesselpart.cache.VesselCache;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;


@Service("startVoyagingService")
public class StartVoyagingService implements ExecutionListener, Serializable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 4149621500319226872L;
    private static final String topic = "activiti/vessel/voyaging";
    @Autowired
    private AWSIotMqttClient awsIotMqttClient;
    @Autowired
    private VesselCache vesselCache;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        //notify the  vessel device of start or continuing reported data
        logger.info("*************startVoyagingService****************");
        logger.info(new Date() + " : 进入Voyaging");
        Map<String, Object> vars = execution.getVariables();
        String vid = vars.get("vid").toString();
        //call voyaging-service for vessel device.
        String curStatus = vesselCache.getVesselShadows()
                .get(vid).getStatus();
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        //从JsonFactory创建一个JsonGenerator生成器的实例
        if ("Voyaging".equals(curStatus)) {
            logger.info("next.");
            payloadObjectNode.put("msgType", "NOT_FIRST");
            payloadObjectNode.put("from", vid);
            logger.debug("payload :" + payloadObjectNode.toString());
            AWSIotMessage pub = new MessagePublisher(topic, AWSIotQos.QOS0, payloadObjectNode.toString());
            try {
                awsIotMqttClient.publish(pub);
            } catch (AWSIotException e) {
                e.printStackTrace();
            }

        }
    }

}
