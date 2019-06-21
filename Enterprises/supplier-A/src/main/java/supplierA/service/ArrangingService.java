package supplierA.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import supplierA.domain.Logistics;
import supplierA.domain.Order;
import supplierA.repos.OrderRepository;
import supplierA.repos.CommonRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("arrangingService")
@SuppressWarnings("all")
public class ArrangingService implements JavaDelegate ,Serializable{

    private  final Logger logger = LoggerFactory.getLogger(ArrangingService.class);


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

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

        //#####add the logistics selection logic

        String category = "variable-destination"; // can read the policy from other places
//        String category = "fixed-destination";

        HashMap<String, Object> payload =  new HashMap<String, Object>();
        payload.put("category" , category);
        payload.put("spid" , spid);
//        String orgId = pvars.get("orgId").toString();
        String url = commonRepository.getSlcContextPath()+"/supplier/"+orgId+"/"+spid+"/logistic";
        HashMap<String , Object> res = restClient.queryLogistic(url, payload);

        //TODO:Manually select suppliers
        String lOrgId = res.get("lOrgId").toString();
        if(lOrgId != null){
            runtimeService.setVariable(spid , "lOrgId" , lOrgId);
            runtimeService.setVariable(spid , "lCategory" , category);
            logger.info("Logistics Specified : orgId = "+lOrgId);
        }else{
            logger.debug("No Logistics Specified");
        }

        //#########

        String orderId = pvars.get("orderId").toString();
//        String lOrgId = pvars.get("lOrgId").toString();

      logger.debug("logistics category :" , category);

        Order order = orderRepository.findById(orderId);
        Logistics logistics = order.generateLogistic(null,lOrgId , null , null ,category , null , commonRepository.getLocation());
        String url1 = commonRepository.getSlcContextPath()+"/supplier/"+orgId+"/"+spid+"/arrange";
        restClient.arrange(url1 , logistics);
        logger.info("Generate logistics successfully");
    }
}
