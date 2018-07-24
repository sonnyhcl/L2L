package vesseldevA.repos;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vesseldevA.domain.*;
import vesseldevA.services.pubSub.VesselPublisher;
import vesseldevA.services.shadow.VesselDevice;
import vesseldevA.util.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AsyncTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);
    private String stompUpdateShadowTopic = "/topic/vessel/shadow/update";
    private String stompInitShadowTopic = "/topic/vessel/shadow/init";

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    public CommonRepository commonRepository;

    @Autowired
    private AWSClientService awsClientService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrajectoryRepository trajectoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private TrackService trackService;

    public void initVesselState(String vid) throws InterruptedException, IOException, AWSIotException {
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        String updateShadowTopic = deviceClient.getAwsUpdateShadowTopic();
        VesselDevice vesselDevice = deviceClient.getVesselDevice();

        objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("wantedProperties", SimpleBeanPropertyFilter
                .filterOutAllExcept("vesselState", "destinations", "positionIndex", "simuStartTime", "vid", "nextPortIndex", "status")));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Track track =  trackService.findTrack(vid);
        List<Step> steps = track.getSteps();

        vesselDevice.updatePositionIndex(0);
        vesselDevice.updateStatus("Initiating");
        vesselDevice.updateNextPortIndex(0);

        //when vessel just starts out , preprocess the port data.
        //update default delay time at each port
        long defaultDelayMs = commonRepository.getDefautDelayHour() * 60 * 60 * 1000;
        long simuStartMs = new Date().getTime();
        vesselDevice.updateSimuStartTime(DateUtil.ms2dateStr(simuStartMs));
        long rawStartMs = DateUtil.str2date(steps.get(0).getVesselStates().get(0)
                .getTimeStamp())
                .getTime();
        int i = 0;
        List<Destination> destinations = new ArrayList<Destination>();
        for(Step step : steps){
            Destination d = new Destination();
            String ts = step.getVesselStates().get(step.getVesselStates().size()-1).getTimeStamp();
            d.setName(step.getNextPort());
            d.setEstiAnchorTime(ts);
            d.setEstiArrivalTime(ts);
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
            destinations.add(d);
        }
        logger.info("first time to adjust destinations : " + destinations);
        vesselDevice.updateDestinations(destinations);
        VesselState initVesselState = trajectoryRepository.findVesselState(0).deepCopy();
        vesselDevice.updateVesselState(initVesselState);

        String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
        logger.debug("init--->payload : " + payload);
        AWSIotMessage initPub = new VesselPublisher(updateShadowTopic, AWSIotQos.QOS0, payload);
        deviceClient.getAwsIotMqttClient().publish(initPub);
    }

//    @Async
    public void reportStep(String vid) throws InterruptedException, AWSIotException, JsonProcessingException {
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        String updateShadowTopic = deviceClient.getAwsUpdateShadowTopic();
        VesselDevice vesselDevice = deviceClient.getVesselDevice();

        vesselDevice.updateStatus("Voyaging");

        Track track =  trackService.findTrack(vid);
        long zoomInVal = commonRepository.getZoomInVal();
        int curTrackIndex = vesselDevice.getNextPortIndex();
        Step curStep = track.getSteps().get(curTrackIndex);
        Destination curDest = vesselDevice.getDestinations().get(curTrackIndex);
        List<VesselState> stepVesselStates = curStep.getVesselStates();
        int pos = vesselDevice.getPositionIndex();
        int size = stepVesselStates.size();
        int i = 0;
        long x = 0;
        long y = 0;
        while(i < size){
            vesselDevice.updatePositionIndex(pos+1);
            x = System.currentTimeMillis();
//            logger.info("<" + i + ">" + (x - y));
            VesselState curVesselState = stepVesselStates.get(i).deepCopy();// deep copy to avoid modifying the vesselStates map
            long sleepMs = 0;
            //TODO:calculate elapse time between current state and next state
            if (i < size - 1) {
                long curStateMs = DateUtil.str2date(stepVesselStates.get(i).getTimeStamp()).getTime();
                long nextStateMs = DateUtil.str2date(stepVesselStates.get(i+ 1).getTimeStamp()).getTime();
                sleepMs = nextStateMs - curStateMs;
            }
            //TODO: modify the date in vessel state.
            long curMs = new Date().getTime();
            long startMs = DateUtil.str2date(vesselDevice.getSimuStartTime()).getTime();
            String newStateTime = DateUtil.ms2dateStr(startMs + (curMs - startMs) * zoomInVal);
            curVesselState.setTimeStamp(newStateTime);
            vesselDevice.updateVesselState(curVesselState);

            //TODO: sync vessel device data to vessel shadow
            String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
            AWSIotMessage pub = new VesselPublisher(updateShadowTopic, AWSIotQos.QOS0, payload);
            deviceClient.getAwsIotMqttClient().publish(pub);

            i++;
            y = x;
            Thread.sleep(sleepMs/zoomInVal);
        }


        if(curTrackIndex <= track.getSteps().size()-1){
            Destination arrivalDest = vesselDevice.getDestinations().get(curTrackIndex);
            Location arrivalLoc = locationRepository.findLocation(arrivalDest.getName());
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

            if (curTrackIndex == vesselDevice.getDestinations().size()-1) {
                logger.info("The vessel arrives at the last port --"+arrivalDest);
            }else{
                logger.info("Arriving at  port : " + arrivalDest);
            }

            int nextTrackIndex = curTrackIndex+1;
            vesselDevice.updateNextPortIndex(nextTrackIndex);
            //TODO: sync vessel device data to vessel shadow
            String payload = "{\"state\":{\"desired\":" + objectMapper.writeValueAsString(vesselDevice) + "}}";
            AWSIotMessage pub = new VesselPublisher(updateShadowTopic, AWSIotQos.QOS0, payload);
            deviceClient.getAwsIotMqttClient().publish(pub);
            logger.info("reach , nextPortIndex = " + nextTrackIndex);
        }
    }


    public void simuAD(String vid){
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = deviceClient.getVesselDevice();
        //TODO: Timing simulation of anchoring and docking status of the ship
        long zoomVal = commonRepository.getZoomInVal();
        long simuMs = DateUtil.str2date(vesselDevice.getSimuStartTime()).getTime();
        Destination curDest = vesselDevice.getDestinations().get(vesselDevice.getNextPortIndex() - 1);
        long delayDurationMs = DateUtil.TimeMinus(curDest.getEstiDepartureTime() , curDest.getEstiAnchorTime());
        long x = System.currentTimeMillis();
        logger.debug("delayDuraion : "+delayDurationMs);
        try {
                Thread.sleep(delayDurationMs/zoomVal);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        long y = System.currentTimeMillis();
        logger.debug("delay : "+(y-x)/1000+"s");
    }
    public void trackOnce(String vid) throws InterruptedException, AWSIotException, JsonProcessingException {
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = deviceClient.getVesselDevice();
        Track track =  trackService.findTrack(vid);
        int curTrackIndex = vesselDevice.getNextPortIndex();
        List<Destination> destinations = vesselDevice.getDestinations();
        int dSize = destinations.size();
        while(curTrackIndex < dSize){
            reportStep(vid);
            simuAD(vid);
        }
    }

    @Async
    public void vesselSensor(String vid) throws InterruptedException, AWSIotException, IOException {
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = deviceClient.getVesselDevice();
//        while(true){
            initVesselState(vid);
            trackOnce(vid);
            logger.debug("finish once track -- "+vid);
//       }
    }

    @Async
    public void collectData(String vid){
        DeviceClient deviceClient = awsClientService.findDeviceClient(vid);
        VesselDevice vesselDevice = deviceClient.getVesselDevice();
        while(true){
            try {
                simpMessagingTemplate.convertAndSend(stompUpdateShadowTopic ,  vesselDevice);
//                logger.info("send to front-end");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
