package msc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import msc.Repos.PairRepository;
import msc.domain.Order;
import msc.domain.SupplierProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class RelayStation {

    private static final Logger logger = LoggerFactory.getLogger(RelayStation.class);

    @Autowired
    private PairRepository pairRepository;

 @Autowired
 private DecisionMaking decisionMaking;
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
        Order  order1 =  decisionMaking.order(orgId  , pid  , order);
        return new ResponseEntity<Order>(order1, HttpStatus.OK);
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
