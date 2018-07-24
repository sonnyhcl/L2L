package logisticsA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsA.domain.Logistics;
import logisticsA.domain.WagonShadow;
import logisticsA.repos.CommonRepository;
import logisticsA.repos.LogisticsRepository;
import logisticsA.repos.WagonShadowRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("runningPostService")
public class RunningPostService implements ExecutionListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4885656684805353238L;
    private static final Logger logger = LoggerFactory.getLogger(RunningPostService.class);

    @Autowired
    private  RuntimeService runtimeService;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LogisticsRepository logisticsRepository;

    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        logger.info("--RunningPostService--");
        String pid = exec.getProcessInstanceId();
        Map<String, Object> vars = exec.getVariables();
        String logisticsId = vars.get("logisticId").toString();
        Logistics logistics = logisticsRepository.findById(logisticsId);
        String logisStatus = logistics.getStatus();
        runtimeService.setVariable(pid , "status" , logisStatus);
        //TODO: notify vessel of arrival.
        if(logisStatus.equals("Meeting")){
            logger.debug("logistics status : Meeting!");
        }
        if(logisStatus.equals("Missing")){
            logger.debug("logistics status : Missing!");
        }
    }

}
