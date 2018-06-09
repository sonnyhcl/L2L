package vesselpart.vessel.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.rest.service.api.RestResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vesselpart.awsiot.MessagePublisher;
import vesselpart.cache.VesselCache;
import vesselpart.entity.Location;
import vesselpart.entity.VPort;
import vesselpart.cache.GlobalVariables;
import vesselpart.vessel.domain.VesselShadow;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

@Service("initVesselProcessService")
public class InitVesselProcessService implements ExecutionListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 298971968212119081L;
    private final Logger logger = LoggerFactory.getLogger(InitVesselProcessService.class);

    private static final String topic = "activiti/vessel/init";

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private VesselCache vesselCache;
    @Autowired
    private AWSIotMqttClient awsIotMqttClient;
    @Autowired
    private ObjectMapper objectMapper;
    @SuppressWarnings("unchecked")
    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        logger.info("*************InitVesselProcessService****************");
        //bind vid to vessel shadow
        Map<String, Object> vars = execution.getVariables();
        String vpid = execution.getProcessInstanceId();
        Map<String , VesselShadow>  vesselShadowMap = vesselCache.getVesselShadows();
        String vid = vars.get("vid").toString();
        int defaultDelayHour = Integer.parseInt(execution.getVariable("defaultDelayHour").toString());
        int zoomInVal =  Integer.parseInt(execution.getVariable("zoomInVal").toString());
       // VesselShadow vesselShadow = vesselShadowMap.get(vId);
       // if(vesselShadow == null){
        VesselShadow vesselShadow = new VesselShadow();
        //}else{
//        }
        vesselShadow.setVpid(vpid);
        vesselShadow.setVid(vid);
        vesselShadowMap.put(vid , vesselShadow);
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("msgType" , "INIT");
        payloadObjectNode.put("from" , vid);
        payloadObjectNode.put("defaultDelayHour" , defaultDelayHour);
        payloadObjectNode.put("zoomInVal" , zoomInVal);
        logger.debug("payload :"+payloadObjectNode.toString());
        AWSIotMessage pub = new MessagePublisher(topic, AWSIotQos.QOS0, payloadObjectNode.toString());
        try {
            awsIotMqttClient.publish(pub);
        } catch (AWSIotException e) {
                e.printStackTrace();
        }

        Map<String, Object> addiVars = new HashMap<String, Object>();
        addiVars.put("isMeet", false);
        addiVars.put("isMissing", false);
        runtimeService.setVariables(vpid, addiVars);

    }

}
