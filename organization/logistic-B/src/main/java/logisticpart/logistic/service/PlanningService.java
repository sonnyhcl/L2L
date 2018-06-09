package logisticpart.logistic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import logisticpart.cache.LogisticCache;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;

@Service("planningService")
public class PlanningService implements JavaDelegate ,Serializable{

    private  final Logger logger = LoggerFactory.getLogger(PlanningService.class);


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LogisticCache supplierCache;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String lpid = delegateExecution.getProcessInstanceId();
        logger.info("************************PlanningService************************"+lpid);

        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(lpid);

        logger.info("Planning path successfully.");
    }
}
