package logisticA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticA.domain.WagonShadow;
import logisticA.repos.CommonRepository;
import logisticA.repos.WagonShadowRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service("runningPostService")
public class RunningPostService implements ExecutionListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4885656684805353238L;
    private static final Logger logger = LoggerFactory.getLogger(RunningPostService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        logger.info("--RunningPostService--");
        String pid = exec.getProcessInstanceId();
        Map<String, Object> vars = exec.getVariables();
        String wid = vars.get("wid").toString();
        WagonShadow wagon = wagonShadowRepository.findById(wid);
        String status = wagon.getStatus();
        runtimeService.setVariable(pid , "status" , status);
        //TODO: notify vessel of arrival.
        if(status.equals("Meeting")){
            logger.debug("wagon status : Meeting!");
//            String arrivalUrl = commonRepository.getLvcContextPath()+"/logistic/"+commonRepository.getOrgId()+"/"+pid+"/arrival";
//            String rep = restClient.notifyMsg(arrivalUrl);
//            logger.info(rep);
        }
        if(status.equals("Missing")){
            logger.debug("wagon status : Missing!");
//            String arrivalUrl = commonRepository.getLvcContextPath()+"/logistic/"+commonRepository.getOrgId()+"/"+pid+"/arrival";
//            String rep = restClient.notifyMsg(arrivalUrl);
//            logger.info(rep);
        }
    }

}
