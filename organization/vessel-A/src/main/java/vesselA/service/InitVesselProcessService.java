package vesselA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vesselA.controller.AwsClient;
import vesselA.repos.CommonRepository;
import vesselA.domain.VesselShadow;
import vesselA.repos.ShadowRepository;

import java.io.Serializable;
import java.util.*;

@Service("initVesselProcessService")
public class InitVesselProcessService implements ExecutionListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 298971968212119081L;
    private final Logger logger = LoggerFactory.getLogger(InitVesselProcessService.class);


    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private  ShadowRepository shadowRepository;
    @Autowired
    private AwsClient awsClient;
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        logger.info("--InitVesselProcessService--");
        //bind vid to vessel shadow
        Map<String, Object> vars = execution.getVariables();
        String pid = execution.getProcessInstanceId();
        String vid = vars.get("vid").toString();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        vesselShadow.setVpid(pid);
        //TODO send init message to vessel device
        awsClient.sendInitiation("INIT" , vid , vesselShadow.getDefaultDelayHour() , vesselShadow.getZoomInVal());
        Map<String, Object> addiVars = new HashMap<String, Object>();
        addiVars.put("status" , "Initiating");
        addiVars.put("applyId" , "NONE");
        addiVars.put("nextNav" , true);
        runtimeService.setVariables(pid, addiVars);

    }

}
