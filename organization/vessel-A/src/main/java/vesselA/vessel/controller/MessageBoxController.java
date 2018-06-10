package vesselA.vessel.controller;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vesselA.repos.VesselCache;
import vesselA.util.DateUtil;
import vesselA.vessel.domain.Port;
import vesselA.vessel.domain.VesselShadow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * business locgic for interaction between  Monitor and Vessel
 * @author bqzhu
 */
@RestController
public class MessageBoxController extends AbstractController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String topic = "activiti/vessel/delay";
    @Autowired
    private AWSIotMqttClient awsIotMqttClient;

    @Autowired
    private VesselCache vesselCache;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/vessel/delay/{vid}", method = RequestMethod.PUT)
    public String delay(@PathVariable("vid") String vid, @RequestBody HashMap<String, Object> mp) {
        int delayHour = Integer.parseInt(mp.get("delayHour").toString());
        int postponeHour = Integer.parseInt(mp.get("postponeHour").toString());
        //modify vesselShadow and sync shadow to device
        VesselShadow vesselShadow = vesselCache.getVesselShadows().get(vid);
        if (vesselShadow == null) {
            return "There is no corresponding process instance for this vessel identifier！";
        }
        List<Port> ports = vesselShadow.getPorts();
        int curPortIndex = vesselShadow.getNextPortIndex();
        if(vesselShadow.getStatus().equals("Anchoring") || vesselShadow.getStatus().equals("Docking")){
            curPortIndex--;
        }
        long zoomVal = vesselCache.getZoomVal();
        long simuMs = DateUtil.str2date(vesselShadow.getSimuStartDateStr()).getTime();
        long curMs = (new Date().getTime()-simuMs)*zoomVal+simuMs;

        if(vesselShadow.getStatus().equals("Voyaging") || vesselShadow.getStatus().equals("Anchoring")) {
            logger.debug("The vessel received an delay/postpone message during the voyage");
            long gapMs = -1;
            for (int i = 0; i < ports.size(); i++) {
                Port p = ports.get(i);
                String newStAnchoringTime = null;
                String newReachTime = null;
                String newDepartureTime = null;

                if (i == curPortIndex) {
                    newStAnchoringTime = p.getStAnchorTime();
                    newReachTime = DateUtil.date2str(DateUtil.
                            transForDate(DateUtil.str2date(p.getEstiReachTime()).getTime() + delayHour * 60 * 60 * 1000));
                    long anchringDuration = DateUtil.TimeMinus(newReachTime, newStAnchoringTime);
                    if (anchringDuration < 0) {
                        return "invalid delay duration : arrival time can not be earlier than anchor time";
                    }
                    gapMs = anchringDuration + postponeHour * 60 * 60 * 1000;
                    newDepartureTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getEstiDepartureTime()).getTime() + gapMs));
                    long dockingDuration = DateUtil.TimeMinus(newDepartureTime, newReachTime);
                    if (dockingDuration < 0) {
                        return "invalid delay duration : newDepartureTime can not be before newReachTime";
                    }
                    //Determine if the arrival time is later than the current time.
                    if(DateUtil.str2date(p.getEstiReachTime()).getTime() <= curMs) {
                        logger.debug("Invalid arrival time : required to be later than current time ");
                    }
                }

                if (i > curPortIndex) {
                    newStAnchoringTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getStAnchorTime()).getTime() + gapMs));
                    newReachTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getEstiReachTime()).getTime() + gapMs));
                    newDepartureTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getEstiDepartureTime()).getTime() + gapMs));
                }
                p.setEstiReachTime(newStAnchoringTime);
                p.setEstiReachTime(newReachTime);
                p.setEstiReachTime(newDepartureTime);
                ports.set(i, p);
            }
            String payload = null;
            ObjectNode payloadObjectNode = objectMapper.createObjectNode();
            payloadObjectNode.put("msgType", "delay");
            payloadObjectNode.put("from", "vessel-app");
            payloadObjectNode.putPOJO("vesselShadow", vesselShadow);
            payload = payloadObjectNode.toString();
            //update vessel shadow at device end.
            try {
                awsIotMqttClient.publish(topic, payload);
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        } else if(vesselShadow.getStatus().equals("Docking")) {
            logger.debug("The vessel received an delay/postpone message during the AnchoringOrDocking");
            if(delayHour != 0){
                delayHour = 0;
            }
            long gapMs = 0;
            for(int i = 0 ; i < ports.size();i++){
                String newStAnchoringTime = null;
                String newReachTime = null;
                String newDepartureTime = null;
                Port p = ports.get(i);
                if(i == curPortIndex ){
                    newStAnchoringTime = p.getStAnchorTime();
                    newReachTime = p.getEstiReachTime();
                    newDepartureTime = DateUtil.date2str(DateUtil.
                            transForDate(DateUtil.str2date(p.getEstiDepartureTime()).getTime() + postponeHour * 60 * 60 * 1000));
                    long dockingDuration= DateUtil.TimeMinus(newDepartureTime,newReachTime);
                    if(dockingDuration < 0){
                        return "invalid postpone duration : departure time can not be earlier than the time in port";
                    }

                    gapMs = dockingDuration;

                    //determine if the departure time is later than cur Ms
                    if(DateUtil.str2date(p.getEstiDepartureTime()).getTime() <= curMs) {
                        logger.debug("Invalid departure time : required to be later than current time ");
                    }

                }

                if(i > curPortIndex) {
                    newStAnchoringTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getStAnchorTime()).getTime() + gapMs));
                    newReachTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getEstiReachTime()).getTime() + gapMs));
                    newDepartureTime = DateUtil.date2str(DateUtil
                            .transForDate(DateUtil.str2date(p.getEstiDepartureTime()).getTime() + gapMs));
                }


                p.setEstiReachTime(newStAnchoringTime);
                p.setEstiReachTime(newReachTime);
                p.setEstiReachTime(newDepartureTime);
                ports.set(i, p);
            }
            String payload = null;
            ObjectNode payloadObjectNode = objectMapper.createObjectNode();
            payloadObjectNode.put("msgType", "postpone");
            payloadObjectNode.put("from", "vessel-app");
            payloadObjectNode.putPOJO("vesselShadow", vesselShadow);
            payload = payloadObjectNode.toString();
            //update vessel shadow at device end.
            try {
                awsIotMqttClient.publish(topic, payload);
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        } else{
            return "The current situation is not considered!(the current status of the vessel is ignored)";
        }

        // delay/postpone event to coordinator
        return "Success";
    }
}

