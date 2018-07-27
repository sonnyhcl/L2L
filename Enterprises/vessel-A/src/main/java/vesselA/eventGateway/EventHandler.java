package vesselA.eventGateway;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vesselA.coordinator.AwsClient;
import vesselA.domain.Destination;
import vesselA.domain.IoTSetting;
import vesselA.repos.ShadowRepository;
import vesselA.coordinator.StompClient;
import vesselA.domain.VesselShadow;
import vesselA.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventHandler {
    private static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;
    private StompClient stompClient;
    private AwsClient awsClient;
    private ShadowRepository shadowRepository;



    public EventHandler(Environment environment, AwsClient awsClient, ShadowRepository shadowRepository , StompClient stompClient) throws IOException, AWSIotException {
        this.stompClient = stompClient;
        this.shadowRepository = shadowRepository;
        this.awsClient = awsClient;
        String fileName = environment.getProperty("awsiot.iotSettings");
        logger.debug("--" + fileName + "--");
        String path = this.getClass().getResource("/").getPath() + "data/" + fileName;
        List<IoTSetting> ioTSettings = CsvUtil.readIoTSettings(path);
        for (int i = 0; i < ioTSettings.size(); i++) {
            IoTSetting ioTSetting = ioTSettings.get(i);
            VesselShadow vs = new VesselShadow();
            vs.setId(ioTSetting.getId());
            shadowRepository.save(vs);
            EventDispatcher eventDispatcher = new EventDispatcher(ioTSetting.getCustomTopic() + "#", AWSIotQos.QOS0);
            eventDispatcher.setEventHandler(this);
            awsClient.getAwsIotMqttClient().subscribe(eventDispatcher);
            //TODO : add other subscribers into MqttClient
            EventDispatcher eventDispatcher1 = new EventDispatcher(ioTSetting.getDefaultTopic() + "#", AWSIotQos.QOS0);
            eventDispatcher1.setEventHandler(this);
            awsClient.getAwsIotMqttClient().subscribe(eventDispatcher1);
        }

    }

    public void changeStatus(AWSIotMessage message) throws IOException {
        JsonNode rootNode = objectMapper.readTree(message.getStringPayload());
        String vid = rootNode.findValue("vid").asText();
        String msgType = rootNode.findValue("msgType").asText();
        String status = rootNode.findValue("status").asText();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        String oldStatus = vesselShadow.getStatus();
        logger.debug("---changeStatus----" + msgType + "--" + status);

        ShadowWithProcesses spr = shadowRepository.findRegisteredProcessesById(vid);
        String pid = spr.getPid();
        if (msgType.equals("DOCKING_END")) {
            logger.debug("--DOCKING_END--");
            Task task = taskService.createTaskQuery().processInstanceId(pid).taskName("AnchoringOrDocking").singleResult();
            logger.debug("Complete AnchoringOrDocking Task. pid = " + pid + " : " + task.toString());
            taskService.complete(task.getId());
        }

        if (msgType.equals("VOYAGING_END")) {
            logger.debug("--VOYAGING_END-- ");
            Task task = taskService.createTaskQuery().processInstanceId(pid).taskName("Voyaging").singleResult();
            taskService.complete(task.getId());
            logger.debug("Complete Voyaging Task. pid = " + pid);
            Destination curPort = vesselShadow.getDestinations().get(vesselShadow.getStepIndex());
            stompClient.sendCurrentPort("admin", "/topic/voyage/end",  curPort ,  status , null);
        }

        if(msgType.equals("ANCHORING_END")){
            logger.debug("--ANCHORING_END --");
        }

        vesselShadow.setStatus(status);
        logger.debug("status changed from" + oldStatus + " to " + status);
    }


    public void vesselShadowForwarding(AWSIotMessage message) throws IOException {
        JsonNode root = objectMapper.readTree(message.getStringPayload());
        String vid = root.get("state").get("desired").findValue("vid").asText();
        String startTime = root.get("state").get("desired").findValue("startTime").asText();
        int stepIndex = root.get("state").get("desired").findValue("stepIndex").asInt();
        String status = root.get("state").get("desired").findValue("status").asText();
        Double longitude = root.get("state").get("desired").findValue("longitude").asDouble();
        Double latitude = root.get("state").get("desired").findValue("latitude").asDouble();
        Double velocity = root.get("state").get("desired").findValue("velocity").asDouble();
        String timeStamp = root.get("state").get("desired").findValue("timeStamp").asText();
        VesselShadow vesselShadow = shadowRepository.findById(vid);

        vesselShadow.setStartTime(startTime);
        vesselShadow.setStepIndex(stepIndex);
        vesselShadow.setStatus(status);
        //TODO : extract vessel state
        vesselShadow.updateState(longitude, latitude, velocity, timeStamp);

        //TODO : extract vessel destination
        JsonNode destinationsNode = root.get("state").get("desired").get("destinations");
        if (destinationsNode.asText().equals("null")) {
            destinationsNode = null;
        }
        if (destinationsNode != null) {
            List<Destination> destinations = new ArrayList<Destination>();
            for (int i = 0; i < destinationsNode.size(); i++) {
                Destination d = objectMapper.readValue(destinationsNode.get(i).toString(), Destination.class);
                destinations.add(d);
            }
            vesselShadow.setDestinations(destinations);
        }
        //TODO: publish vessel shadow to monitor
        stompClient.sendVesselShadow("admin", "/topic/vesselShadow", vesselShadow);
        if(timeStamp.equals(startTime)){
            //TODO: publish valid ports to monitor
            stompClient.sendValidPorts("admin","/topic/ports", vesselShadow.getDestinations());
        }
    }
}
