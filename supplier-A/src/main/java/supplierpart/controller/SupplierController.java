package supplierpart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import supplierpart.cache.SupplierCache;
import supplierpart.domain.Order;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SupplierController extends AbstractController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SupplierCache supplierCache;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @param orgId
     * @param order
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/supplier/{orgId}/process-instances/MsgStartSupplier", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Order> startSupplierProc(@PathVariable("orgId") String orgId,
                                                   @RequestBody Order order) throws JsonProcessingException {
        logger.info(orgId+" : MsgStartSupplier");
        logger.info("***********startSupplierProc***********");
        //TODO: start manager process instance by message MsgStartSupplier.
        Map<String , Object> vars = new HashMap<String , Object>();
        vars.put("orgId" , order.getSOrgId());
        vars.put("orderId" , order.getId());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartSupplier", vars);

        //TODO: register supplier process instance in collaboration group.
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        sendData.put("mOrgId" , order.getMOrgId());
        sendData.put("mpid" , order.getMpid());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = objectMapper.writeValueAsString(sendData);
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = supplierCache.getCoBasePath()+"/supplier/"+orgId+"/"+pi.getId()+"/match";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    /**
     *
     * @param payload
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/supplier/logistic", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> queryLogistic(@RequestBody HashMap<String, Object> payload) throws IOException {
        String category = payload.get("category").toString();
        String spid = payload.get("spid").toString();
        logger.info("Seleted logistic type  : "+category+" : "+spid);
        logger.info("***********queryLogistic***********");

        Map<String, Object> pvars =  runtimeService.getVariables(spid);
        String orgId = pvars.get("orgId").toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String url = supplierCache.getCoBasePath()+"/"+orgId+"/"+spid+"/logistic";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();
        JsonNode rootNode = objectMapper.readTree(body);
        //TODO:Manually select suppliers
        runtimeService.setVariable(spid , "logisticId" , rootNode.findValue("logisticId").asText());
        logger.info(response.getBody());
        return response;
    }


}
