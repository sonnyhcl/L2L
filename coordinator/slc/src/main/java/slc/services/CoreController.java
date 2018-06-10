package slc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private SupplierRepository supplierRepository;



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
        SupplierPart sp = supplierRepository.findByOrgId(orgId);
        SupplierProcessInstance spi = new SupplierProcessInstance(pid , orgId , null  , logistic.getId());
        Pair pair = new Pair(spi, null);
        pairRepository.register(pair);

        //TODO: Starting Logistic based on message.
        String lOrgId = logistic.getLOrgId();
        LogisticPart logisticPart = logisticRepository.findByLOrgId(lOrgId);
        String url = logisticPart.getUrl()+"/logistic/"+lOrgId+"/process-instances/MsgStartLogistic";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String  logisticId = pid+ CommonUtil.getGuid();
        String wid = "W"+CommonUtil.getGuid();
        logistic.setWid(wid);
        logistic.setId(logisticId);
        logistic.setLongitude(sp.getLongitude());
        logistic.setLatitude(sp.getLatitude());

        HttpEntity<Logistic> requestEntity = new HttpEntity<Logistic>(logistic, headers);
        ResponseEntity<Logistic> response = restTemplate.postForEntity(url ,requestEntity , Logistic.class);
        logger.info(response.getBody().toString());

        return new ResponseEntity<Logistic>(logistic , HttpStatus.OK);
    }


    @RequestMapping(value = "/supplier/{orgId}/{pid}/logistic", method = RequestMethod.POST)
    public String queryLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                 @RequestBody HashMap<String, Object> payload){

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : logistic from supplier-app : "+orgId+"--PID: "+pid);
        String category = payload.get("category").toString();
        String logisticId = null;
        switch (category){
            case "fixed-rendezvous" :
                logisticId = "LA1001";
                break;
            case "variable-rendezvous" :
                logisticId = "LB1003";
                break;
            default :
                break;
        }
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();

        if(logisticId != null){
            payloadObjectNode.put("lOrgId", logisticId);
            payloadObjectNode.put("lCategory", category);
        }
        String res = payloadObjectNode.toString();
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
    public String managerDispature(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
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
