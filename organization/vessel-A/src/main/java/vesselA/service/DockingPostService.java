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
import vesselA.domain.Application;
import vesselA.domain.VesselShadow;
import vesselA.repos.ApplicationRepository;
import vesselA.repos.ShadowRepository;

import java.io.Serializable;
import java.util.HashMap;
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

    @Autowired
    private ApplicationRepository applicationRepository;


    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        logger.info("--DockingPostService--");
        String pid = exec.getProcessInstanceId();
        Map<String, Object> vars = exec.getVariables();
        String vid = vars.get("vid").toString();
        String applyId = vars.get("applyId").toString();
        logger.debug("applyId : "+applyId);

        //TODO: 通知前端离港
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        logger.debug("status : "+vesselShadow.getStatus());
        String pname = vesselShadow.getDestinations().get(vesselShadow.getNextPortIndex()-1).getName();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("pname" , pname);
        simpMessagingTemplate.convertAndSendToUser( "admin","/topic/dockEnd" , payload);
        logger.debug(pname);
        if(vesselShadow.getNextPortIndex() == vesselShadow.getDestinations().size()-1){
            logger.debug("到达最后一个港口：　"+pname);
            runtimeService.setVariable(pid , "nextNav" , false);
        }

        //TODO: 如果流程发出了申请
        if(!applyId.equals("NONE")){
            Application application = applicationRepository.findById(applyId);
            String rend = application.getRendezvous();
            //TODO: 如果交付未结束　，检查交付状态
            String applyStatus = application.getStatus();
            if(!(applyStatus.equals("Missing") || applyStatus.equals("Meeting"))){//排除结束状态
                logger.debug("rend"+rend+" applyStatus : "+applyStatus);
                String deliveryStatus = restClient.checkDeiveryStatus(pid);

                payload = objectMapper.createObjectNode();
                switch (deliveryStatus){
                    case "MISSING" :
                        application.setStatus("Missing");
                        //TODO: notify logistic of "Missing"
                        HashMap<String , Object> msgBody = new HashMap<String , Object>();
                        msgBody.put("eventType" , "MISSING");
                        restClient.notifyMsg(pid , "Missing" , msgBody);
                        logger.debug("vessel status : Missing");
                        payload.put("pid" , pid);
                        payload.put("msgType" , "MISSING");
                        logger.debug("send \"MISSING\" message to monitor : ");
                        simpMessagingTemplate.convertAndSendToUser( "admin","/topic/missing" , payload);
                        break;
                    case "NOT_MISSING" :
                        if(rend.equals(pname)){ //在船离港时检查船是否交货成功
                            application.setStatus("Meeting");
                            //TODO: notify logistic of "Missing"
                            msgBody = new HashMap<String , Object>();
                            msgBody.put("eventType" , "MEETING");
                            restClient.notifyMsg(pid , "Meeting" , msgBody);
                            logger.debug("vessel status : Meeting");
                            payload.put("pid" , pid);
                            payload.put("msgType" , "MEET");
                            payload.put("rendezvous" , application.getRendezvous());
                            logger.debug("send \"MEET\" message to monitor : ");
                            simpMessagingTemplate.convertAndSendToUser( "admin","/topic/meeting" , payload);
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
    }

}
