package iot.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import iot.domain.*;
import iot.repos.CommonRepository;
import iot.repos.LocationRepository;
import iot.repos.TrackRepository;
import iot.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AsyncTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);

    @Autowired
    public CommonRepository commonRepository;

    @Autowired
    private AWSClientService awsClientService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TrackRepository trackRepository;

    public void initVesselIoT(String vid , int delayHour , int zoomInVal ) throws InterruptedException, IOException, AWSIotException {
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        String updateShadowTopic = ioTClient.getUpdateAWSShadowTopic();
        VesselDevice vesselDevice = ioTClient.getVesselDevice();

        objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("wantedProperties", SimpleBeanPropertyFilter
                .filterOutAllExcept("vid", "longitude", "latitude", "velocity", "timeStamp", "startTime", "stepIndex", "status","destinations")));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Track track = trackRepository.findTrack(vid);
        List<Step> steps = track.getSteps();
        vesselDevice.updateStatus("Voyaging");
        vesselDevice.updateStepIndex(0);

        //when vessel just starts out , preprocess the port data.
        //update default delay time at each port
        long defaultDelayMs = commonRepository.getDefautDelayHour() * 60 * 60 * 1000;
        long startMs = new Date().getTime();
        vesselDevice.updateStartTime(DateUtil.ms2dateStr(startMs));
        long rawStartMs = DateUtil.str2date(steps.get(0).getVesselStates().get(0)
                .getTimeStamp())
                .getTime();
        int i = 0;
        List<Destination> destinations = new ArrayList<Destination>();
        for (Step step : steps) {
            Destination d = new Destination();
            String ts = step.getVesselStates().get(step.getVesselStates().size() - 1).getTimeStamp();
            d.setName(step.getNextPort());
            d.setEstiAnchorTime(ts);
            d.setEstiArrivalTime(ts);
            long rawArrivalMs = DateUtil.str2date(d.getEstiArrivalTime()).getTime();
            long simuArrivalMs = startMs + rawArrivalMs - rawStartMs;
            simuArrivalMs += i * defaultDelayMs;
            String simuArrivalStr = DateUtil.ms2dateStr(simuArrivalMs);
            long simuDepartureMs = simuArrivalMs + defaultDelayMs;
            String simuDepartureStr = DateUtil.ms2dateStr(simuDepartureMs);
            if (simuArrivalStr != null && simuDepartureStr != null) {
                d.setEstiAnchorTime(simuArrivalStr);
                d.setEstiArrivalTime(simuArrivalStr);
                d.setEstiDepartureTime(simuDepartureStr);
            } else {
                logger.error("Exists the error in preprocessing the port data.");
            }
            destinations.add(d);
            i++;
        }
        logger.info("First time to adjust destinations : " + destinations);
        vesselDevice.updateDestinations(destinations);
        VesselState vs = steps.get(0).getVesselStates().get(0).deepCopy();
        vesselDevice.updateState(vs);
    }

    public void reportStep(String vid) throws InterruptedException, AWSIotException, JsonProcessingException {
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        String updateShadowTopic = ioTClient.getUpdateAWSShadowTopic();
        VesselDevice vesselDevice = ioTClient.getVesselDevice();

        Track track = trackRepository.findTrack(vid);
        long zoomInVal = commonRepository.getZoomInVal();
        int stepIdx = vesselDevice.getStepIndex();
        Step curStep = track.getSteps().get(stepIdx);
        Destination curDest = vesselDevice.getDestinations().get(stepIdx);
        List<VesselState> stepVesselStates = curStep.getVesselStates();
        int size = stepVesselStates.size();
        int i = 0;
        long x = 0;
        long y = 0;
        vesselDevice.updateStatus("Voyaging");
        while (i < size) {
            x = System.currentTimeMillis();
            VesselState curVesselState = stepVesselStates.get(i).deepCopy();// deep copy to avoid modifying the vesselStates map
            long sleepMs = 0;
            //TODO:calculate elapse time between current state and next state
            if (i < size - 1) {
                long curStateMs = DateUtil.str2date(stepVesselStates.get(i).getTimeStamp()).getTime();
                long nextStateMs = DateUtil.str2date(stepVesselStates.get(i + 1).getTimeStamp()).getTime();
                sleepMs = nextStateMs - curStateMs;
            }
            //TODO: modify the date in vessel state.
            long curMs = new Date().getTime();
            long startMs = DateUtil.str2date(vesselDevice.getStartTime()).getTime();
            String newStateTime = DateUtil.ms2dateStr(startMs + (curMs - startMs) * zoomInVal);
            if(stepIdx ==0 && i == 0){
                curVesselState.setTimeStamp(vesselDevice.getStartTime());
            }else{
                curVesselState.setTimeStamp(newStateTime);
            }
            vesselDevice.updateState(curVesselState);

            //TODO: sync vessel device data to vessel shadow
//            logger.debug(curVesselState.toString());
            String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
            AWSIotMessage pub = new EventPublisher(updateShadowTopic, AWSIotQos.QOS0, payload);
            ioTClient.getAwsIotMqttClient().publish(pub);

            i++;
            y = x;
            Thread.sleep(sleepMs / zoomInVal);
            y = System.currentTimeMillis();
//            logger.debug((y-x)+"ms");

        }


        if (stepIdx <= track.getSteps().size() - 1) {
            Destination arrivalDest = vesselDevice.getDestinations().get(stepIdx);
            //TODO: Determine if the status is  anchoring or docking
            ObjectNode payloadObjectNode = objectMapper.createObjectNode();
            if (arrivalDest.getEstiAnchorTime().equals(arrivalDest.getEstiArrivalTime())) {
                logger.info("Transiting into Docking status");
                vesselDevice.updateStatus("Docking");
                payloadObjectNode.put("msgType", "DOCKING");
            } else {
                logger.info("Transiting into Anchoring status");
                vesselDevice.updateStatus("Anchoring");
                payloadObjectNode.put("msgType", "ANCHORING");
            }
            changeStatus(ioTClient, ioTClient.getUpdateStatusTopic(), "VOYAGING_END", vesselDevice);

            if (stepIdx == vesselDevice.getDestinations().size() - 1) {
                logger.info("The vessel arrives at the last port --" + arrivalDest);
            } else {
                logger.info("Arriving at  port : " + arrivalDest);
            }
        }
    }


    public void simuAD(String vid) {
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = ioTClient.getVesselDevice();
        //TODO: Timing simulation of anchoring and docking status of the ship
        int stepIdx = vesselDevice.getStepIndex();
        long zoomVal = commonRepository.getZoomInVal();
        long simuMs = DateUtil.str2date(vesselDevice.getStartTime()).getTime();
        Destination curDest = vesselDevice.getDestinations().get(stepIdx);
        long startMs = DateUtil.str2date(vesselDevice.getStartTime()).getTime();
        while (true) {
            long curMs = (new Date().getTime() - simuMs) * zoomVal + simuMs;
            long nextMs = curMs + 1000 * zoomVal;
            if (vesselDevice.getStatus().equals("Anchoring")) {
                long newReachMs = DateUtil.str2date(curDest.getEstiArrivalTime()).getTime();
                logger.debug("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + "new reach time : " + curDest.getEstiArrivalTime());
                if (newReachMs > curMs && newReachMs <= nextMs) {
                    vesselDevice.updateStatus("Docking");
                    changeStatus(ioTClient, ioTClient.getUpdateStatusTopic(), "ANCHORING_END", vesselDevice);
                }
            } else if (vesselDevice.getStatus().equals("Docking")) {
                long newDepartureMs = DateUtil.str2date(curDest.getEstiDepartureTime()).getTime();
                logger.info("Current time : " + DateUtil.ms2dateStr(curMs) + " Next time : " + DateUtil.ms2dateStr(nextMs) + " New arrival time : " + curDest.getEstiDepartureTime());
                if (newDepartureMs > curMs && newDepartureMs <= nextMs) {
                    //send depature message to vessel process
                    changeStatus(ioTClient, ioTClient.getUpdateStatusTopic(), "DOCKING_END", vesselDevice);
                    logger.info("Docking  , departure");
                    break;
                }
            }
            int nextStepIndex = stepIdx + 1;
            vesselDevice.updateStepIndex(nextStepIndex);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void trackOnce(String vid, int delayHour , int zoomInVal ) throws InterruptedException, AWSIotException, IOException {
        commonRepository.setZoomInVal(zoomInVal);
        commonRepository.setDefautDelayHour(delayHour);
        initVesselIoT(vid , delayHour , zoomInVal);
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = ioTClient.getVesselDevice();
        Track track = trackRepository.findTrack(vid);
        int stepIdx = vesselDevice.getStepIndex();
        List<Destination> destinations = vesselDevice.getDestinations();
        int dSize = destinations.size();
        while (stepIdx < dSize) {
            reportStep(vid);
            long x = System.currentTimeMillis();
            simuAD(vid);
            long y = System.currentTimeMillis();
            logger.debug((y-x)/1000+"s");

        }
    }


    /**
     * change status
     *
     * @param changeStatusTopic
     * @param msgType
     * @param device
     */
    private void changeStatus(IoTClient ioTClient, String changeStatusTopic, String msgType, VesselDevice device) {
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("msgType", msgType);
        payloadObjectNode.put("vid", device.getVid());
        payloadObjectNode.put("status", device.getStatus());
        payloadObjectNode.put("nextPortIndex", device.getStepIndex()+1);
        AWSIotMessage depPub = new EventPublisher(changeStatusTopic, AWSIotQos.QOS0, payloadObjectNode.toString());
        try {
            ioTClient.getAwsIotMqttClient().publish(depPub);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }



    /**
     * update vessel Shadow
     *
     * @param rootNode
     */
    public void delay(String vid , JsonNode rootNode) throws IOException {
        IoTClient ioTClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = ioTClient.getVesselDevice();
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
