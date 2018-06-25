package supplierA.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import supplierA.repos.CommonRepository;
import supplierA.domain.Order;
import supplierA.repos.OrderRepository;
import supplierA.service.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@SuppressWarnings("all")
public class SupplierController extends AbstractController {
    @Autowired
    private RestClient restClient;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping("/hello")
    public String hello(){
        logger.debug("hello ,supplier");
        return "hello , supplier";
    }


    /**
     * @param orgId
     * @param order
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/{orgId}/process-instances/MsgStartSupplier", method = RequestMethod.POST, produces = "application/json")
    public String startSupplierProc(@PathVariable("orgId") String orgId,
                                                   @RequestBody Order order) throws JsonProcessingException {
        logger.info(orgId+" : MsgStartSupplier");
        logger.info("***********startSupplierProc***********");
        //TODO: start manager process instance by message MsgStartSupplier.
        Map<String , Object> vars = new HashMap<String , Object>();
        vars.put("orgId" , order.getSOrgId());
        vars.put("orderId" , order.getId());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartSupplier", vars);

        //TODO:save order
        order.setSpid(pi.getId());
        orderRepository.save(order);

        //TODO: register supplier process instance in msc.
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        sendData.put("mOrgId" , order.getMOrgId());
        sendData.put("mpid" , order.getMpid());
        sendData.put("ordId" , order.getId());
        String url = commonRepository.getMscContextPath()+"/supplier/"+orgId+"/"+pi.getId()+"/match";
        String rep = restClient.matchManager(url , sendData);
        logger.info(rep);
        return "Start supplier successfully.";
    }

    /**
     *
     * @param payload
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/logistic/category", method = RequestMethod.POST, produces = "application/json")
    public HashMap<String , Object> queryLogistic(@RequestBody HashMap<String, Object> payload) throws IOException {
        logger.info("--POST /logistic/category--"+payload.toString());
        String category = payload.get("category").toString();
        String spid = payload.get("spid").toString();

        Map<String, Object> pvars =  runtimeService.getVariables(spid);
        String orgId = pvars.get("orgId").toString();
        String url = commonRepository.getSlcContextPath()+"/supplier/"+orgId+"/"+spid+"/logistic";
        HashMap<String , Object> res = restClient.queryLogistic(url, payload);

        //TODO:Manually select suppliers
        String lOrgId = res.get("lOrgId").toString();
        if(lOrgId != null){
            runtimeService.setVariable(spid , "lOrgId" , lOrgId);
            String lCategory = res.get("lCategory").toString();
            runtimeService.setVariable(spid , "lCategory" , lCategory);
            logger.info("Logistic Specified : orgId = "+lOrgId);
        }else{
            logger.debug("No Logistic Specified");
        }
        return res;
    }


}
