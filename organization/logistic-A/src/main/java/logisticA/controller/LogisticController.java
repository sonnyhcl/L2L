package logisticA.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticA.domain.Logistic;
import logisticA.domain.WagonShadow;
import logisticA.repos.CommonRepository;
import logisticA.repos.LogisticRepository;
import logisticA.repos.WagonShadowRepository;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@SuppressWarnings("all")
public class LogisticController extends AbstractController {
    private  final Logger logger = LoggerFactory.getLogger(LogisticController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LogisticRepository logisticRepository;


    /**
     * receive message StartLogistic from SLC.
     * @param orgId
     * @param msgType
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/logistic/{orgId}/process-instances/MsgStartLogistic", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Logistic> startLogisticProc(@PathVariable("orgId") String orgId,
                                                    @RequestBody Logistic logistic) throws JsonProcessingException {
        logger.info("***********startLogisticProc***********");
        logger.info(orgId+" : MsgStartLogistic");

        //TODO: start Logistic process instance by message MsgStartLogistic.
        Map<String , Object> vars = new HashMap<String , Object>();
        vars.put("orgId" , logistic.getLOrgId());
        vars.put("logisticId" , logistic.getId());
        //TODO: Save to wagon shadow
        WagonShadow wagonShadow = wagonShadowRepository.findById(logistic.getWid());
        WagonShadow ws = null;
        if(wagonShadow == null){
            double sLongitude = logistic.getLongitude();
            double sLatitude = logistic.getLatitude();
            ws = new WagonShadow(logistic.getWid() , sLongitude , sLatitude , 0.0 , null , null , null);
            wagonShadowRepository.save(ws);
        }
        vars.put("wid" , logistic.getWid());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartLogistic", vars);
        ws.setWpid(pi.getId());

        //TODO: shake hands with Supplier
        HashMap<String , Object> sendData = new HashMap<String , Object>();

        sendData.put("sOrgId" , logistic.getSOrgId());
        sendData.put("spid" , logistic.getSpid());
        sendData.put("wid" , logistic.getWid()); // default "null"
        sendData.put("logisticId" , logistic.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = objectMapper.writeValueAsString(sendData);
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = commonRepository.getSlcContextPath()+"/logistic/"+orgId+"/"+pi.getId()+"/match";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());


        return new ResponseEntity<Logistic>(logistic, HttpStatus.OK);
    }

}
