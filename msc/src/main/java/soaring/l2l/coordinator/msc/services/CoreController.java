package soaring.l2l.coordinator.msc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import soaring.l2l.coordinator.msc.Repos.PairRepository;
import soaring.l2l.coordinator.msc.Repos.SupplierRepository;
import soaring.l2l.coordinator.msc.domain.ManagerPart;
import soaring.l2l.coordinator.msc.domain.Order;
import soaring.l2l.coordinator.msc.domain.Pair;
import soaring.l2l.coordinator.msc.domain.SupplierPart;

import java.util.HashMap;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private SupplierRepository supplierRepository;


    /**
     * *****************from Manager participant****************************************
     */

    /**
     * receive order from manager
     * Send StartSupplier to manager-part for starting one manager process.
     * @param orgId
     * @param pid
     * @param order
     * @return
     */
    @RequestMapping(value = "/manager/{orgId}/{pid}/order", method = RequestMethod.POST)
    public ResponseEntity<Order> order(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                           @RequestBody Order order){

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received order from manager-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping  company.
        ManagerPart managerPart = new ManagerPart(orgId,pid);
        Pair pair = new Pair(managerPart , null);
        pairRepository.register(pair);

        //TODO: According to the spare parts information, choose the best supplier according to the corresponding policy
        String spName = order.getSpName();
        String sOrgId = null;
        switch(spName){
            case "缸盖" :
                sOrgId = "S1001";
                break;
            case "锤子" :
                sOrgId = "S1002";
                break;
            case "钢板" :
                sOrgId = "S1003";
                break;
            default :
                break;

        }

        //select a supplier for order.
        order.setSOrgId(sOrgId);

        //TODO: Starting Supplier based on message.

        String url = ManagerPart.getUrl()+"/supplier/"+orgId+"/process-instances/MsgStartSupplier";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Order> requestEntity = new HttpEntity<Order>(order, headers);
        ResponseEntity<Order> response = restTemplate.postForEntity(url ,requestEntity , Order.class);
        logger.info(response.getBody().toString());

        return new ResponseEntity<Order>(order , HttpStatus.OK);
    }


    /**
     * *****************from Supplier participant****************************************
     */

    /**
     * receive match from supplier and  return response to other participant.
     * @param orgId
     * @param pid
     * @param payload
     * @return
     */
    @RequestMapping(value = "/supplier/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String managerDispature(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                       @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from manager-app : "+orgId+"--PID: "+pid);

        //TODO:Ship shakes hands with the affiliated shipping management company.
        String mOrgId = payload.get("mOrgId").toString();
        String mpid = payload.get("mpid").toString();
        SupplierPart sp = supplierRepository.findByOrgId(orgId).deepCopy();
        sp.setPid(pid);
        pairRepository.match(mOrgId , mpid , sp);
        logger.info(pairRepository.getPairs().toString());
        return "M-S match successfully";
    }

}
