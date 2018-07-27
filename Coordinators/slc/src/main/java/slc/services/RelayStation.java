package slc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import slc.domain.*;
import slc.repos.LogisticsPartRepository;
import slc.repos.PairRepository;
import slc.repos.SupplierPartRepository;
import slc.util.CommonUtil;

import java.util.HashMap;

@RestController
@SuppressWarnings("all")
public class RelayStation {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PairRepository pairRepository;


    @Autowired
    private DecisionMaking decisionMaking;
    @Autowired
    private LogisticsPartRepository logisticsPartRepository;

    @Autowired
    private SupplierPartRepository supplierPartRepository;

    @RequestMapping("/hello")
    public String hello(){
        logger.debug("hello ,slc");
        return "hello , slc";
    }



    /**
     * *****************from Supplier participant****************************************
     */

    /**
     * receive messages from supplier and  return response to other participant.
     * V-M
     * @param orgId
     * @param pid
     * @param payload
     * @return
     */
    @RequestMapping(value = "/supplier/{orgId}/{pid}/arrange", method = RequestMethod.POST)
    public  ResponseEntity<Logistics>  arrangeLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                                       @RequestBody Logistics logistics) throws JsonProcessingException {

        logger.debug("Received message : MsgStartLogistic from supplier-app : "+orgId+"--PID: "+pid);
        Logistics logistics1 =  decisionMaking.arrangeLogistics(orgId , pid , logistics);
        return new ResponseEntity<Logistics>(logistics1, HttpStatus.OK);
    }


    @RequestMapping(value = "/supplier/{orgId}/{pid}/logistic", method = RequestMethod.POST)
    public  HashMap<String , Object> queryLogistics(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                 @RequestBody HashMap<String, Object> payload){

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : logistic from supplier-app : "+orgId+"--PID: "+pid);
        String category = payload.get("category").toString();
        String logisticId = null;
        switch (category){
            case "variable-destination" :
                logisticId = "LA1001";
                break;
            case "fixed-destination" :
                logisticId = "LA1001";
                break;
            default :
                break;
        }

        HashMap<String , Object> res = new HashMap<String , Object>();
        if(logisticId != null){
            res.put("lOrgId", logisticId);
            res.put("lCategory", category);
        }
        return res;
    }




    /**
     * *****************from Logistics participant****************************************
     */

    /**
     * receive messages from logistic and  return response to other participant.
     * M-V
     * @param orgId
     * @param pid
     * @param msgType
     * @param payload
     * @return
     */
    @RequestMapping(value = "/logistics/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String match(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                         @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from logistic-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String sOrgId = payload.get("sOrgId").toString();
        String spid = payload.get("spid").toString();
        String wid = payload.get("wid").toString();
        String logisticId = payload.get("logisticId").toString();
        LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(sOrgId);
        LogisticProcessInstance lpi = new LogisticProcessInstance(pid , orgId , logisticId ,wid);
        pairRepository.match(sOrgId , sOrgId , lpi);
        logger.info(pairRepository.getPairs().toString());
        return "S-L match successfully";
    }




}
