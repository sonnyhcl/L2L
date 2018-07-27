package managerA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import managerA.domain.Application;
import managerA.domain.Order;
import managerA.domain.SparePart;
import managerA.repos.ApplicationRepository;
import managerA.repos.CommonRepository;
import managerA.repos.OrderRepository;
import managerA.repos.SparePartRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;


@Service("orderService")
public class OrderService implements JavaDelegate,Serializable {

    private  final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String pid = delegateExecution.getProcessInstanceId();
        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String orgId = pvars.get("orgId").toString();

        //TODO : generate order from application.
        Application application = applicationRepository.findById(pvars.get("applyId").toString());
        Order order =  application.generateOrder(null , null , null);
        //TODO: Supplementary order information
        SparePart sparePart = sparePartRepository.findByName(order.getSpName());
        order.setSpWight(sparePart.getWeight());
       // Omitted timeStamp
        String url = commonRepository.getMscContextPath()+"/manager/"+orgId+"/"+pid+"/order";
        String rep = restClient.postOrder(url , order);
//        String rep = restClient.test();
        logger.info(rep);
        logger.info("Generate order successfully.");
    }
}
