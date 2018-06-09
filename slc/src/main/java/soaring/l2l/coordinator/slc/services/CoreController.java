package soaring.l2l.coordinator.slc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import soaring.l2l.coordinator.slc.domain.LogisticPart;
import soaring.l2l.coordinator.slc.domain.Pair;
import soaring.l2l.coordinator.slc.domain.SupplierPart;
import soaring.l2l.coordinator.slc.repos.LogisticRegistory;
import soaring.l2l.coordinator.slc.repos.PairRepository;
import soaring.l2l.coordinator.slc.repos.SupplierRepository;

import java.util.HashMap;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticRegistory logisticRegistory;

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
    @RequestMapping(value = "/supplier/{orgId}/{pid}/MsgStartLogistic", method = RequestMethod.POST)
    public String startLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                  @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : MsgStartLogistic from supplier-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping  company.
        SupplierPart supplierPart = pairLibraryService.findBySOrgId(fromOrgId).deepCopy();
        supplierPart.setPid(fromPid);
        Pair pair = new Pair(supplierPart, null);
        pairLibraryService.register(pair);

        //TODO: Starting Logistic based on message.
        String orgId = payload.get("logisticId").toString();
        String url = LogisticPart.getUrl()+"/logistic/"+orgId+"/process-instances/MsgStartLogistic";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        payload.put("sOrgId" , fromOrgId);
        payload.put("spid" , fromPid);
        payload.put("sLongitude" , supplierPart.getLongitude());
        payload.put("sLatitude" , supplierPart.getLatitude());
        String body = objectMapper.writeValueAsString(payload);
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url ,requestEntity , String.class);
        logger.info(response.getBody());

        return response.toString();
    }


    @RequestMapping(value = "/supplier/{orgId}/{pid}/logistic", method = RequestMethod.POST)
    public String queryLogistic(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                 @RequestBody HashMap<String, Object> payload){

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : logistic from supplier-app : "+orgId+"--PID: "+pid);
        String category = payload.get("category").toString();
        String logisticId = null;
        switch (category){
            case "固定目的地" :
                logisticId = "L1001";
                break;
            case "可变目的地" :
                logisticId = "L1003";
                break;
            default :
                break;
        }
        ObjectNode payloadObjectNode = objectMapper.createObjectNode();
        payloadObjectNode.put("logisticId" , logisticId);
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
        String mOrgId = payload.get("sOrgId").toString();
        String mpid = payload.get("spid").toString();
        String wid = payload.get("wid").toString();
        LogisticPart logisticPart = pairLibraryService.findByLOrgId(fromOrgId).deepCopy();
        logisticPart.setPid(fromPid);
        logisticPart.setWid(wid);
        pairLibraryService.match(mOrgId , mpid , logisticPart);
        logger.info(pairLibraryService.getPairs().toString());
        return "M-S match successfully";
    }




}
