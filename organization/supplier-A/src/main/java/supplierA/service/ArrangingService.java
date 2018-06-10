package supplierA.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import supplierA.domain.Logistic;
import supplierA.domain.Order;
import supplierA.repos.OrderRepository;
import supplierA.util.CommonUtil;
import supplierA.repos.CommonRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;

@Service("arrangingService")
@SuppressWarnings("all")
public class ArrangingService implements JavaDelegate ,Serializable{

    private  final Logger logger = LoggerFactory.getLogger(ArrangingService.class);


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String spid = delegateExecution.getProcessInstanceId();

        //TODO:get process variables
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(spid);
        String orgId = pvars.get("orgId").toString();
        String orderId = pvars.get("orderId").toString();
        String lOrgId = pvars.get("lOrgId").toString();
        String lCategory = pvars.get("lCategory").toString();



        Order order = orderRepository.findById(orderId);

        Logistic logistic = order.generateLogistic(null,lOrgId , null , null ,lCategory , null , 0.0 , 0.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<Logistic> requestEntity = new HttpEntity<Logistic>(logistic, headers);
        String url = commonRepository.getSlcContextPath()+"/supplier/"+orgId+"/"+spid+"/arrange";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());

        logger.info("Generate order successfully :");
    }
}
