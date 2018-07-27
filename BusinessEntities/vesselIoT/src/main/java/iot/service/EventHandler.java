package iot.service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iot.domain.IoTClient;
import iot.repos.CommonRepository;
import iot.repos.LocationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("all")
@Service
public class EventHandler {
    private static Logger logger = Logger.getLogger(EventHandler.class);
    private static final AWSIotQos topicQos = AWSIotQos.QOS0;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AsyncTaskService asyncTaskService;

    public EventHandler(AWSClientService awsClientService) throws AWSIotException {
        List<IoTClient> ioTClients = awsClientService.getIotClients();
        for (IoTClient ioTClient : ioTClients) {
            EventSubscriber sub = new EventSubscriber(ioTClient.getCustomTopic() + "#", AWSIotQos.QOS0);
            sub.setEventHandler(this);
            logger.debug(sub.getEventHandler().toString());
            ioTClient.getAwsIotMqttClient().subscribe(sub, false);
        }

    }

    /**
     * Init vessel shadow
     *
     * @param msgJson
     * @throws InterruptedException
     * @throws IOException
     * @throws AWSIotException
     */
    public void track(String vid , JsonNode rootNode) throws InterruptedException, IOException, AWSIotException {
        logger.debug("start track of vid = "+vid);
        int delayHour = rootNode.findValue("defaultDelayHour").asInt();
        int zoomInVal = rootNode.findValue("zoomInVal").asInt();
        asyncTaskService.trackOnce(vid , delayHour ,  zoomInVal);
    }


}
