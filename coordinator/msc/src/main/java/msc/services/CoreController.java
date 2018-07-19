package msc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import msc.Repos.CraneRepository;
import msc.Repos.ManagerRepository;
import msc.domain.*;
import msc.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import msc.Repos.PairRepository;
import msc.Repos.SupplierRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class CoreController {

    private static final Logger logger = LoggerFactory.getLogger(CoreController.class);

    @Autowired
    private  RestClient restClient;

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CraneRepository craneRepository;
    @RequestMapping(value = "/{testId}/hello",method = RequestMethod.POST)
    public  String hello(@PathVariable("testId") String testId ,  @RequestBody HashMap<String, Object> payload ){
        logger.debug("hello ,msc--"+testId);
        return "hello , mscc";
    }
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public  String gethello(){
        logger.debug("hello ,msc--");
        return "hello , msc";
    }

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
        //TODO:generate ordId
        String orderId = pid+ CommonUtil.getGuid();
        //TODO:Ship shakes hands with the affiliated shipping  company.
        ManagerPart managerPart = managerRepository.findByOrgId(orgId);
        ManagerProcessInstance mpi = new ManagerProcessInstance(pid , orgId);
        mpi.setOrderId(orderId);
        if(pairRepository.isRegistried(orgId , pid) == false){
            Pair pair = new Pair(mpi , null);
            pairRepository.register(pair);
        }


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

        //TODO: According to crane information , screen effective ports.
        List<String> validDests = new ArrayList<String>();
        List<String> invalidDests = new ArrayList<String>();
        List<String> destinations = order.getDestinations();
        for(int i = 0 ; i < destinations.size() ; i++){
            String d = destinations.get(i);
            double wlim = craneRepository.queryWeightLimit(d);
            if(order.getSpWight() <= wlim){
                validDests.add(d);
            }else{
                invalidDests.add(d);
            }
        }
        order.setDestinations(validDests);
        ObjectNode payload = objectMapper.createObjectNode();
        payload.putPOJO("invalidDestinations" , invalidDests);
        simpMessagingTemplate.convertAndSend("/topic/destinations/invalid", payload);
        logger.debug("send invalidDestinations "+invalidDests.toString());


        //TODO: Starting Supplier based on message.
        SupplierPart supplierPart = supplierRepository.findByOrgId(sOrgId);
        order.setId(orderId);
        String url = supplierPart.getUrl()+"/api/"+orgId+"/process-instances/MsgStartSupplier";
        String rep = restClient.startSupplier(url , order);
        logger.info(rep);
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
    public String matchManager(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                       @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from manager-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String mOrgId = payload.get("mOrgId").toString();
        String mpid = payload.get("mpid").toString();
        String ordId = payload.get("ordId").toString();
        SupplierProcessInstance spi = new SupplierProcessInstance(pid , orgId);
        spi.setOrderId(ordId);
        pairRepository.match(mOrgId , mpid , spi);
        logger.info(pairRepository.getPairs().toString());
        return "M-S match successfully";
    }

}
