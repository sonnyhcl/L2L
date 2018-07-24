package slc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import slc.domain.*;
import slc.repos.LogisticRepository;
import slc.repos.PairRepository;
import slc.repos.SupplierRepository;

import java.util.HashMap;

@RestController
@SuppressWarnings("all")
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private SupplierRepository supplierRepository;

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
    public  ResponseEntity<Logistic>  startLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                @RequestBody Logistic logistic) throws JsonProcessingException {

        logger.debug("Received message : MsgStartLogistic from supplier-app : "+orgId+"--PID: "+pid);
        //TODO:Supplier shakes hands with logistic.
        //TODO: allocate wid , logisticId
        String wid = "W"+CommonUtil.getGuid();
        String  logisticId = pid+ CommonUtil.getGuid();

        SupplierPart sp = supplierRepository.findByOrgId(orgId);
        SupplierProcessInstance spi = new SupplierProcessInstance(pid , orgId , null  , logisticId);
        spi.setLogisticId(logistic.getId());
        if(pairRepository.isRegistried(orgId , pid) == false){
            Pair pair = new Pair(spi, null);
            pairRepository.register(pair);
        }

        //TODO: Starting Logistic based on message.
        String lOrgId = logistic.getLOrgId();
        LogisticPart logisticPart = logisticRepository.findByLOrgId(lOrgId);
        String url = logisticPart.getUrl()+"/api/"+lOrgId+"/process-instances/MsgStartLogistic";
        logistic.setWid(wid);
        logistic.setId(logisticId);
        String rep = restClient.startLogistic(url , logistic);
        logger.info(rep);

        return new ResponseEntity<Logistic>(logistic , HttpStatus.OK);
    }


    @RequestMapping(value = "/supplier/{orgId}/{pid}/logistic", method = RequestMethod.POST)
    public  HashMap<String , Object> queryLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                 @RequestBody HashMap<String, Object> payload){

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : logistic from supplier-app : "+orgId+"--PID: "+pid);
        String category = payload.get("category").toString();
        String logisticId = null;
        switch (category){
            case "variable-rendezvous" :
                logisticId = "LA1001";
                break;
            case "fixed-rendezvous" :
                logisticId = "LB1003";
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
     * *****************from Logistic participant****************************************
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
    @RequestMapping(value = "/logistic/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String match(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                         @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from logistic-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String sOrgId = payload.get("sOrgId").toString();
        String spid = payload.get("spid").toString();
        String wid = payload.get("wid").toString();
        String logisticId = payload.get("logisticId").toString();
        LogisticPart logisticPart = logisticRepository.findByLOrgId(sOrgId);
        LogisticProcessInstance lpi = new LogisticProcessInstance(pid , orgId , logisticId ,wid);
        pairRepository.match(sOrgId , sOrgId , lpi);
        logger.info(pairRepository.getPairs().toString());
        return "S-L match successfully";
    }




}
