package logisticA.service;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logisticA.awsiot.MessagePublisher;
import logisticA.domain.*;
import logisticA.repos.*;
import logisticA.util.PathUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Service("planningService")
public class PlanningService implements JavaDelegate ,Serializable{

    private static final Logger logger = LoggerFactory.getLogger(PlanningService.class);

    private static final String topic = "activiti/wagon/navigate";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String pid = delegateExecution.getProcessInstanceId();
        logger.info("************************PlanningService************************"+pid);

        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String logisticId = pvars.get("logisticId").toString();
        String orgId =  pvars.get("orgId").toString();
        String wid = pvars.get("wid").toString();


        //TODO: shake hands with Supplier
        Logistic logistic = logisticRepository.findById(logisticId);
        Location origin = locationRepository.findByName(logistic.getSupLoc());
        List<String> destinations = logistic.getDestinations();
        int size = destinations.size();
        RoutePlan routePlan = new RoutePlan();
        for(int i = 0 ; i < size ; i++){
            Location destination = locationRepository.findByName(destinations.get(i));
            //double to string ""+x
            String pathInfo = mapRepository.PlanPath(""+origin.getLongitude() , ""+origin.getLatitude()
                    , ""+destination.getLongitude() , ""+destination.getLatitude());
            JsonNode pathNode = null;
            try {
                 pathNode = objectMapper.readTree(pathInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path route = PathUtil.extractPath(pathNode);
            Rendezvous r = new Rendezvous(destination.getName() , route);
            routePlan.getRendezvousList().add(r);
            logger.debug("route plan : "+ i);
        }

        //TODO : decide rendezvous
        String url = commonRepository.getLvcContextPath()+"/logistic/"+orgId+"/"+pid+"/route/decide";
        Rendezvous response = restClient.decide(url , routePlan);

        //TODO: send route info to navigator for displaying track and simulating running , navigator represents the device side.
        Rendezvous rd = response;
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("msgType" , "RENDEZVOUS");
        payload.put("from" , wid);
        payload.putPOJO("msgBody" , rd);
        simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route" , payload);
        runtimeService.setVariable(pid , "status" , "Running");
        WagonShadow wagonShadow = wagonShadowRepository.findById(wid);
        wagonShadow.setStatus("Running");


    }
}
