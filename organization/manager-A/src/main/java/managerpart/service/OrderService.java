package managerpart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import managerpart.domain.Application;
import managerpart.domain.Order;
import managerpart.repos.ApplicationRepository;
import managerpart.repos.CommonRepository;
import managerpart.repos.OrderRepository;
import managerpart.util.CommonUtil;
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

@Service("orderService")
public class OrderService implements JavaDelegate ,Serializable{

    private  final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String pid = delegateExecution.getProcessInstanceId();
        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String orgId = pvars.get("orgId").toString();

                //TODO : generate order from application.
        String orderId = pid+CommonUtil.getGuid();
        Application application = applicationRepository.findById(pvars.get("applyId").toString());
        Order order =  application.generateOrder(orderId , null , null);
        //Omitted timeStamp

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Order> requestEntity = new HttpEntity<Order>(order, headers);
        String url = commonRepository.getVMCRootPath()+"/manager/"+orgId+"/"+pid+"/order";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());

        logger.info("Generate order successfully ：");
    }
}
