package msc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import msc.Repos.ManagerRepository;
import msc.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import msc.Repos.PairRepository;
import msc.Repos.SupplierRepository;

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

    @Autowired
    private ManagerRepository managerRepository;


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
        ManagerPart managerPart = managerRepository.findByOrgId(orgId);
        ManagerProcessInstance mpi = new ManagerProcessInstance(pid , orgId);
        Pair pair = new Pair(mpi , null);
        pairRepository.register(pair);

        //TODO: According to the spare parts information, choose the best supplier according to the corresponding policy
        String spName = order.getSpName();
        String sOrgId = null;
        switch(spName){
            case "缸盖" :
                sOrgId = "SA1001";
                break;
            case "锤子" :
                sOrgId = "SB1002";
                break;
            case "钢板" :
                sOrgId = "SC1003";
                break;
            default :
                break;

        }

        //select a supplier for order.
        order.setSOrgId(sOrgId);

        //TODO: Starting Supplier based on message.

        SupplierPart supplierPart = supplierRepository.findByOrgId(sOrgId);

        String url = supplierPart.getUrl()+"/supplier/"+orgId+"/process-instances/MsgStartSupplier";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String orderId = pid+ CommonUtil.getGuid();
        order.setId(orderId);
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
        SupplierProcessInstance spi = new SupplierProcessInstance(pid , orgId);
        pairRepository.match(mOrgId , mpid , spi);
        logger.info(pairRepository.getPairs().toString());
        return "M-S match successfully";
    }

}
