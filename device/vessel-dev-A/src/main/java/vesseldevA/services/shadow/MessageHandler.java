package vesseldevA.services.shadow;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vesseldevA.domain.Destination;
import vesseldevA.domain.Location;
import vesseldevA.domain.VesselState;
import vesseldevA.repos.CommonRepository;
import vesseldevA.repos.DestinationRepository;
import vesseldevA.repos.LocationRepository;
import vesseldevA.repos.TrajectoryRepository;
import vesseldevA.services.pubSub.VesselPublisher;
import vesseldevA.util.DateUtil;
import vesseldevA.util.SpringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
@Service
public class MessageHandler {
    private static Logger logger = Logger.getLogger(MessageHandler.class);
    private static final AWSIotQos topicQos = AWSIotQos.QOS0;
    private String updateShadowTopic;

    private String changeStatusTopic;
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AWSIotMqttClient awsIotMqttClient;
    @Autowired
    private VesselDevice vesselDevice;
    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private TrajectoryRepository trajectoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private DestinationRepository destinationRepository;

    public  MessageHandler(@Value("${vessel.topic.updateShadow}") String  updateShadowTopic ,
                   @Value("${vessel.topic.updateStatus}") String changeStatusTopic ){
            logger.debug(updateShadowTopic+"--"+changeStatusTopic);
            this.updateShadowTopic = updateShadowTopic;
            this.changeStatusTopic = changeStatusTopic;
    }


    /**
     * Init vessel shadow
     *
     * @param msgJson
     * @throws InterruptedException
     * @throws IOException
     * @throws AWSIotException
     */
    public void initVesselState(JsonNode msgJson) throws InterruptedException, IOException, AWSIotException {
        objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("wantedProperties", SimpleBeanPropertyFilter
                .filterOutAllExcept("vesselState", "destinations", "positionIndex", "simuStartTime", "vid", "nextPortIndex", "status")));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //read data from repos
        List<Destination> destinations = destinationRepository.getDestinations();
        //Initialization part of the data
        vesselDevice.updatePositionIndex(0);
        vesselDevice.updateStatus("Initiating");
        vesselDevice.updateNextPortIndex(0);

        //when vessel just starts out , preprocess the port data.
        String vid = commonRepository.getVid();
        //update default delay time at each port
        long defautDelayHour = msgJson.findValue("defaultDelayHour").asInt();
        commonRepository.setDefautDelayHour(defautDelayHour);
        long defaultDelayMs = defautDelayHour * 60 * 60 * 1000;
        //set value of zooming in time
        long zoomInVal = msgJson.findValue("zoomInVal").asInt();
        commonRepository.setZoomInVal(zoomInVal);
        long simuStartMs = new Date().getTime();
        vesselDevice.updateSimuStartTime(DateUtil.ms2dateStr(simuStartMs));
        long rawStartMs = DateUtil.str2date(trajectoryRepository
                .findVesselState(vesselDevice.getPositionIndex())
                .getTimeStamp())
                .getTime();
        int i = 0;
        for (Destination d : destinations) {
            long rawArrivalMs = DateUtil.str2date(d.getEstiArrivalTime()).getTime();
            long simuArrivalMs = simuStartMs + rawArrivalMs - rawStartMs;
            simuArrivalMs += i * defaultDelayMs;
            String simuArrivalStr = DateUtil.ms2dateStr(simuArrivalMs);
            long simuDepartureMs = simuArrivalMs + defaultDelayMs;
            String simuDepartureStr = DateUtil.ms2dateStr(simuDepartureMs);
            if (simuArrivalStr != null && simuDepartureStr != null) {
                d.setEstiAnchorTime(simuArrivalStr);
                d.setEstiArrivalTime(simuArrivalStr);
                d.setEstiDepartureTime(simuDepartureStr);
            } else {
                logger.error("exists the error in preprocessing the port data.");
            }
        //    destinations.set(i, d);
            i++;
        }
        logger.info("first time to adjust destinations : " + destinations);
        vesselDevice.updateVid(vid);
        vesselDevice.updateDestinations(destinations);
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
        int size = trajectoryRepository.size();
        long zoomInVal = commonRepository.getZoomInVal();
        int pos = vesselDevice.getPositionIndex();
        int nextPortIndex = vesselDevice.getNextPortIndex();
        Destination nextDest = vesselDevice.getDestinations().get(nextPortIndex);
        long x = 0;
        long y = 0;
        while (pos < size) {
            //TODO:get the current System time
            x = System.currentTimeMillis();
            logger.info("<" + pos + ">" + (x - y));
            VesselState curVesselState = trajectoryRepository.findVesselState(pos).deepCopy();// deep copy to avoid modifying the vesselStates map
            long sleepMs = 0;
            //TODO:calculate elapse time between current state and next state
            if (pos < size - 1) {
                long curStateMs = DateUtil.str2date(trajectoryRepository.findVesselState(pos).getTimeStamp()).getTime();
                long nextStateMs = DateUtil.str2date(trajectoryRepository.findVesselState(pos + 1).getTimeStamp()).getTime();
                sleepMs = nextStateMs - curStateMs;
                logger.info(trajectoryRepository.findVesselState(pos)+":"+trajectoryRepository.findVesselState(pos + 1)+":"+sleepMs+"sleep : " + sleepMs / zoomInVal);
            }
            //TODO:determine if the ship reaches the next port.
            Location nextLoc = locationRepository.findLocation(nextDest.getName());
            boolean isArrival = curVesselState.getLongitude() == nextLoc.getLongitude()
                    && curVesselState.getLatitude() == nextLoc.getLatitude() && nextPortIndex < vesselDevice.getDestinations().size();
            logger.info("index of next port :" + nextPortIndex + " arrival ? :" + isArrival);

            //TODO: modify the date in vessel state.
            long curMs = new Date().getTime();
            long startMs = DateUtil.str2date(vesselDevice.getSimuStartTime()).getTime();
            String newStateTime = DateUtil.ms2dateStr(startMs + (curMs - startMs) * zoomInVal);
            curVesselState.setTimeStamp(newStateTime);
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
                if (nextDest.getEstiAnchorTime().equals(nextDest.getEstiArrivalTime())) {
                    logger.info("Transiting into Docking status"+nextDest.toString());
                    vesselDevice.updateStatus("Docking");
                    payloadObjectNode.put("msgType", "DOCKING");
                } else {
                    logger.info("Transiting into Anchoring status"+nextDest.toString());
                    vesselDevice.updateStatus("Anchoring");
                    payloadObjectNode.put("msgType", "ANCHORING");
                }
                logger.info("Current port : " + nextLoc);
                nextPortIndex++;
                vesselDevice.updateNextPortIndex(nextPortIndex);
                if (nextPortIndex < vesselDevice.getDestinations().size()) {
                    nextDest = vesselDevice.getDestinations().get(nextPortIndex);
                    logger.info("The vessel doesn't arrive at the last port.");
                }else{
                    logger.info("The vessel arrived at the last port.");

                }
                logger.info("Next port : " + nextDest);
                //TODO: sync vessel device data to vessel shadow
                String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
                AWSIotMessage pub = new VesselPublisher(updateShadowTopic, topicQos, payload);
                awsIotMqttClient.publish(pub);
                logger.info("reach , nextPortIndex = " + nextPortIndex);

                //TODO: Timing simulation of anchoring and docking status of the ship
                long zoomVal = commonRepository.getZoomInVal();
                long simuMs = DateUtil.str2date(vesselDevice.getSimuStartTime()).getTime();
                while (true) {
                    curMs = (new Date().getTime() - simuMs) * zoomVal + simuMs;
                    long nextMs = curMs + 1000 * zoomVal;
                    Destination curDest = vesselDevice.getDestinations().get(vesselDevice.getNextPortIndex() - 1);
                    if (vesselDevice.getStatus().equals("Anchoring")) {
                        long newReachMs = DateUtil.str2date(curDest.getEstiArrivalTime()).getTime();
                        logger.debug("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + "new reach time : " + curDest.getEstiArrivalTime());
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
                        long newDepartureMs = DateUtil.str2date(curDest.getEstiDepartureTime()).getTime();
                        logger.info("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + " New arrival time : " + curDest.getEstiDepartureTime());
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

                if(vesselDevice.getNextPortIndex() < vesselDevice.getDestinations().size()){
                    break;
                }

            }
            pos++;
            if (pos == trajectoryRepository.size()) {
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
        JsonNode destinationsNode = rootNode.get("destinations");
        logger.debug("--delay--"+destinationsNode.toString());
        if (destinationsNode!= null) {
            List<Destination> destinations = new ArrayList<Destination>();
            for(int i = 0 ; i < destinationsNode.size() ; i++){
                Destination d = objectMapper.readValue(destinationsNode.get(i).toString() , Destination.class);
                destinations.add(d);
            }
            vesselDevice.updateDestinations(destinations);
        }
    }

}
