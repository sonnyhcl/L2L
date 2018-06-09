package vesseldevice.services.shadow;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vesseldevice.conf.VesselStateCache;
import vesseldevice.domain.Port;
import vesseldevice.domain.VesselState;
import vesseldevice.services.pubSub.VesselPublisher;
import vesseldevice.util.DateUtil;
import vesseldevice.util.MapperUtil;


import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MessageHandler {
    private static Logger logger = Logger.getLogger(MessageHandler.class);
    private static final AWSIotQos topicQos = AWSIotQos.QOS0;
    private static final String updateShadowTopic = "$aws/things/vessel/shadow/update";
    private static final String changeStatusTopic = "activiti/vessel/status/change";
    //    private static final String departurePortTopic = "activiti/vessel/port/departure";
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AWSIotMqttClient awsIotMqttClient;
    @Autowired
    private VesselDevice vesselDevice;
    @Autowired
    private VesselStateCache vesselStateCache;

    /**
     * Init vessel shadow
     *
     * @param msgJson
     * @throws InterruptedException
     * @throws IOException
     * @throws AWSIotException
     */
    public void initVesselState(JsonNode msgJson) throws InterruptedException, IOException, AWSIotException {
        //read data from cache
        List<Port> ports = vesselStateCache.getPorts();
        List<VesselState> vesselStates = vesselStateCache.getVesselStates();
        objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("wantedProperties", SimpleBeanPropertyFilter
                .filterOutAllExcept("vesselState", "ports", "positionIndex", "simuStartDateStr", "vid", "nextPortIndex", "status")));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //Initialization part of the data
        vesselDevice.updatePositionIndex(280);
        vesselDevice.updateStatus("Initiating");
        vesselDevice.updateNextPortIndex(0);

        //when vessel just starts out , preprocess the port data.
        String vid = vesselStateCache.getVid();
        //update default delay time at each port
        long defautDelayHour = msgJson.findValue("defaultDelayHour").asInt();
        vesselStateCache.setDefautDelayHour(defautDelayHour);
        long defaultDelayMs = defautDelayHour * 60 * 60 * 1000;
        //set value of zooming in time
        long zoomInVal = msgJson.findValue("zoomInVal").asInt();
        vesselStateCache.setZoomInVal(zoomInVal);
        long simuStartMs = new Date().getTime();
        vesselDevice.updateSimuStartDateStr(DateUtil.ms2dateStr(simuStartMs));
        long rawStartMs = DateUtil.str2date(vesselStates.get(vesselDevice.getPositionIndex()).getDate()).getTime();
        int i = 0;
        for (Port p : ports) {
            long rawReachMs = DateUtil.str2date(p.getEstiReachTime()).getTime();
            long simuReachMs = simuStartMs + rawReachMs - rawStartMs;
            simuReachMs += i * defaultDelayMs;
            String simuReachStr = DateUtil.ms2dateStr(simuReachMs);
            long simuDepartureMs = simuReachMs + defaultDelayMs;
            String simuDepartureStr = DateUtil.ms2dateStr(simuDepartureMs);
            if (simuReachStr != null && simuDepartureStr != null) {
                p.setEstiReachTime(simuReachStr);
                p.setStAnchorTime(simuReachStr);
                p.setEstiDepartureTime(simuDepartureStr);
            } else {
                logger.error("exists the error in preprocessing the port data.");
            }
            ports.set(i, p);
            i++;
        }
        logger.info("first time to adjust ports : " + ports);
        vesselDevice.updateVid(vid);
        vesselDevice.updatePorts(ports);
        String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
        logger.debug("init--->payload : " + payload);
        AWSIotMessage initPub = new VesselPublisher(updateShadowTopic, topicQos, payload);
        awsIotMqttClient.publish(initPub);
    }

    /**
     * report vessel state data to vessel shadow at an unstable frequency
     *
     * @param msgJson
     * @throws InterruptedException
     * @throws IOException
     * @throws AWSIotException
     */
    public void reportVesselState(JsonNode msgJson) throws InterruptedException, IOException, AWSIotException {
        List<VesselState> vesselStates = vesselStateCache.getVesselStates();

        String msgType = msgJson.findValue("msgType").asText();
        vesselDevice.updateStatus("Voyaging");

        if (msgType.equals("IS_FIRST")) {
            logger.info("received IS_FIRST message from vessel process.");
        } else if (msgType.equals("NOT_FIRST")) {
            logger.info("received NOT_FIRST message from vessel process.");
        } else {
            logger.info("reportVesselState is ignored.");
            return;
        }
        int size = vesselStates.size();
        long zoomInVal = vesselStateCache.getZoomInVal();
        int pos = vesselDevice.getPositionIndex();
        int nextPortIndex = vesselDevice.getNextPortIndex();
        Port nextPort = vesselDevice.getPorts().get(nextPortIndex);
        long x = 0;
        long y = 0;
        while (pos < size) {
            //TODO:get the current System time
            x = System.currentTimeMillis();
            logger.info("<" + pos + ">" + (x - y));
            VesselState curVesselState = vesselStates.get(pos).deepCopy();// deep copy to avoid modifying the vesselStates map
            long sleepMs = 0;
            //TODO:calculate elapse time between current state and next state
            if (pos < size - 1) {
                long curStateMs = DateUtil.str2date(vesselStates.get(pos).getDate()).getTime();
                long nextStateMs = DateUtil.str2date(vesselStates.get(pos + 1).getDate()).getTime();
                sleepMs = nextStateMs - curStateMs;
                logger.info(vesselStates.get(pos)+":"+vesselStates.get(pos + 1)+":"+sleepMs+"sleep : " + sleepMs / zoomInVal);
            }
            //TODO:determine if the ship reaches the next port.
            boolean isArrival = curVesselState.getLongitude() == nextPort.getLongitude()
                    && curVesselState.getLatitude() == nextPort.getLatitude() && nextPortIndex < vesselDevice.getPorts().size();
            logger.info("index of next port :" + nextPortIndex + " arrival ? :" + isArrival);

            //TODO: modify the date in vessel state.
            long curMs = new Date().getTime();
            long startMs = DateUtil.str2date(vesselDevice.getSimuStartDateStr()).getTime();
            String newStateTime = DateUtil.ms2dateStr(startMs + (curMs - startMs) * zoomInVal);
            curVesselState.setDate(newStateTime);
            logger.debug("Current date in vessel state : " + newStateTime);
            vesselDevice.updateVesselState(curVesselState);

            if (isArrival == false) {
                //TODO: sync vessel device data to vessel shadow
                String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
                AWSIotMessage pub = new VesselPublisher(updateShadowTopic, topicQos, payload);
                logger.debug("voyaging--->payload : " + payload);
                awsIotMqttClient.publish(pub);
            } else {
                //TODO: Determine if the status is  anchoring or docking
                ObjectNode payloadObjectNode = objectMapper.createObjectNode();
                if (nextPort.getStAnchorTime().equals(nextPort.getEstiReachTime())) {
                    logger.info("Transiting into Docking status");
                    vesselDevice.updateStatus("Docking");
                    payloadObjectNode.put("msgType", "DOCKING");
                } else {
                    logger.info("Transiting into Anchoring status");
                    vesselDevice.updateStatus("Anchoring");
                    payloadObjectNode.put("msgType", "ANCHORING");
                }
                logger.info("Current port : " + nextPort);
                nextPortIndex++;
                vesselDevice.updateNextPortIndex(nextPortIndex);
                if (nextPortIndex < vesselDevice.getPorts().size()) {
                    nextPort = vesselDevice.getPorts().get(nextPortIndex);
                    logger.info("The vessel doesn't arrive at the last port.");
                }else{
                    logger.info("The vessel arrived at the last port.");

                }
                logger.info("Next port : " + nextPort);
                //TODO: sync vessel device data to vessel shadow
                String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
                AWSIotMessage pub = new VesselPublisher(updateShadowTopic, topicQos, payload);
                awsIotMqttClient.publish(pub);
                logger.info("reach , nextPortIndex = " + nextPortIndex);

                //TODO: Timing simulation of anchoring and docking status of the ship
                long zoomVal = vesselStateCache.getZoomInVal();
                long simuMs = DateUtil.str2date(vesselDevice.getSimuStartDateStr()).getTime();
                while (true) {
                    curMs = (new Date().getTime() - simuMs) * zoomVal + simuMs;
                    long nextMs = curMs + 1000 * zoomVal;
                    Port curPort = vesselDevice.getPorts().get(vesselDevice.getNextPortIndex() - 1);
                    if (vesselDevice.getStatus().equals("Anchoring")) {
                        long newReachMs = DateUtil.str2date(curPort.getEstiReachTime()).getTime();
                        logger.debug("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + "new reach time : " + curPort.getEstiReachTime());
                        if (newReachMs > curMs && newReachMs <= nextMs) {
                            vesselDevice.updateStatus("Docking");
                            payloadObjectNode = objectMapper.createObjectNode();
                            payloadObjectNode.put("msgType", "DEPARTURE_PORT");
                            payloadObjectNode.put("vid", vesselDevice.getVid());
                            payloadObjectNode.put("status", vesselDevice.getStatus());
                            payloadObjectNode.put("nextPortIndex", vesselDevice.getNextPortIndex());
                            AWSIotMessage depPub = new VesselPublisher(changeStatusTopic, topicQos, payloadObjectNode.toString());
                            try {
                                awsIotMqttClient.publish(depPub);
                            } catch (AWSIotException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (vesselDevice.getStatus().equals("Docking")) {
                        long newDepartureMs = DateUtil.str2date(curPort.getEstiDepartureTime()).getTime();
                        logger.info("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + " New arrival time : " + curPort.getEstiDepartureTime());
                        if (newDepartureMs > curMs && newDepartureMs <= nextMs) {
                            //send depature message to vessel process
                            vesselDevice.updateStatus("Voyaging");
                            payloadObjectNode = objectMapper.createObjectNode();
                            payloadObjectNode.put("msgType", "DEPARTURE_PORT");
                            payloadObjectNode.put("vid", vesselDevice.getVid());
                            payloadObjectNode.put("status", vesselDevice.getStatus());
                            payloadObjectNode.put("nextPortIndex", vesselDevice.getNextPortIndex());
                            AWSIotMessage depPub = new VesselPublisher(changeStatusTopic, topicQos, payloadObjectNode.toString());
                            try {
                                awsIotMqttClient.publish(depPub);
                            } catch (AWSIotException e) {
                                e.printStackTrace();
                            }
                            logger.info("Docking  , departure");
                            break;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(vesselDevice.getNextPortIndex() < vesselDevice.getPorts().size()){
                    break;
                }

            }
            pos++;
            if (pos == vesselStates.size()) {
                logger.info("vessel voyaging is completed.");
                break;
            }
            vesselDevice.updatePositionIndex(pos);//Index points to the next data to report.
            y = x;
            Thread.sleep(sleepMs / zoomInVal);
        }

    }

    /**
     * update vessel Shadow
     *
     * @param rootNode
     */
    public void delay(JsonNode rootNode) throws IOException {
        MapperUtil.toVesselDevice(rootNode);
    }
}
