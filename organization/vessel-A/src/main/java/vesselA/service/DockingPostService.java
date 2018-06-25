package vesselA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vesselA.controller.RestClient;
import vesselA.domain.VesselShadow;
import vesselA.repos.ShadowRepository;

import java.io.Serializable;
import java.util.Map;

@Service("dockingPostService")
public class DockingPostService implements ExecutionListener, Serializable {
    private static final long serialVersionUID = 4885656684805353238L;
    private static final Logger logger = LoggerFactory.getLogger(DockingPostService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ShadowRepository shadowRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        logger.info("--DockingPostService--");
        String pid = exec.getProcessInstanceId();
        Map<String, Object> vars = exec.getVariables();
        String vid = vars.get("vid").toString();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        String status = vesselShadow.getStatus();
        logger.debug(vesselShadow.getStatus());
        runtimeService.setVariable(pid , "status" , status);
        String deliveryStatus = restClient.checkDeiveryStatus(pid);
        ObjectNode payload = objectMapper.createObjectNode();
        switch (deliveryStatus){
            case "MISSING" :
                runtimeService.setVariable(pid , "status" , "Missing");
                vesselShadow.setStatus("Missing");
                //TODO: update  status at vessel device end.
                restClient.updateStatus("Missing");

                //TODO: notify logistic of "Missing"
                restClient.notifyMsg(pid , "Missing");

                payload.put("pid" , pid);
                payload.put("msgType" , "MISSING");
                logger.debug("send \"MISSING\" message to monitor : ");
                simpMessagingTemplate.convertAndSendToUser( "admin","/topic/missing" , payload);
                break;
            case "NOT_MISSING" :
                if(status.equals("Meeting")){
                    payload.put("pid" , pid);
                    payload.put("msgType" , "MEET");
                    payload.put("rendezvous" , vesselShadow.getRendezvous());
                    logger.debug("send \"MEET\" message to monitor : ");
                    simpMessagingTemplate.convertAndSendToUser( "admin","/topic/meet" , payload);
                }else{
                    logger.info("There still exists opportunity to meet!");
                }
                break;
            case "NOT_PAIRED" :
                logger.debug("NOT_PAIRED");
                break;
            default:
                    break;
        }
    }

}
