package logisticpart.logistic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticpart.cache.LogisticCache;
import logisticpart.logistic.domain.WagonShadow;
import logisticpart.util.CommonUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
public class LogisticController extends AbstractController {
    private  final Logger logger = LoggerFactory.getLogger(LogisticController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LogisticCache logisticCache;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * receive message StartLogistic from SLC.
     * @param orgId
     * @param msgType
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/logistic/{orgId}/process-instances/{msgType}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> startProcessInstanceByLambdaMessage(@PathVariable("orgId") String orgId,
            @PathVariable("msgType") String msgType, @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        logger.info("***********startProcessInstanceByLambdaMessage***********");
        logger.info(orgId+" : "+msgType);

        //TODO: start Logistic process instance by message MsgStartLogistic.
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage(msgType, payload);

        //TODO: shake hands with Supplier
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        String wid = pi.getId()+ CommonUtil.getGuid();
        sendData.put("sOrgId" , payload.get("sOrgId").toString());
        sendData.put("spid" , payload.get("spid").toString());
        sendData.put("wid" , wid); // default "null"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = objectMapper.writeValueAsString(sendData);
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = logisticCache.getCoBasePath()+"/logistic/"+orgId+"/"+pi.getId()+"/match";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());

        //TODO: Save to wagon shadow
        WagonShadow wagonShadow = logisticCache.getWagonShadows().get("wid");
        if(wagonShadow == null){
            double sLongitude = Double.parseDouble(payload.get("sLongitude").toString());
            double sLatitude = Double.parseDouble(payload.get("sLatitude").toString());
            WagonShadow ws = new WagonShadow(wid , sLongitude , sLatitude , 0.0 , pi.getId() , null , null);
            logisticCache.getWagonShadows().put(wid , ws);
        }
        return new ResponseEntity<String>("Start logistic process-instance Successfully", HttpStatus.OK);
    }

}
