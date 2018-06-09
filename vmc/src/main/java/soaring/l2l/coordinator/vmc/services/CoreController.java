package soaring.l2l.coordinator.vmc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import soaring.l2l.coordinator.vmc.domain.Application;
import soaring.l2l.coordinator.vmc.domain.ManagerPart;
import soaring.l2l.coordinator.vmc.domain.Pair;
import soaring.l2l.coordinator.vmc.domain.VesselPart;
import soaring.l2l.coordinator.vmc.repos.PairLRepository;

import java.util.HashMap;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairLRepository pairLRepository;



    /**
     * *****************from Vessel participant****************************************
     */

    /**
     * receive application from vessel-ppp
     * Send StartManager to manager-part for starting one manager process.
     * @param orgId
     * @param pid
     * @param application
     * @return
     */
    @RequestMapping(value = "/vessel/{orgId}/{pid}/apply", method = RequestMethod.POST)
    public ResponseEntity<Application> vesselDispature(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                           @PathVariable("msgType") String msgType, @RequestBody Application application) throws JsonProcessingException {

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received application for spare part");
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vid = application.getVid();
        VesselPart vesselPart = new VesselPart(orgId , pid , vid);
        Pair pair = new Pair(vesselPart , null);
        pairLRepository.register(pair);

        //TODO:select manager for application

        String mOrgId = "M1001";
        //TODO:send MsgStartManager to manager
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        application.setMOrgId(mOrgId);
//        String body = objectMapper.writeValueAsString(application);
        HttpEntity<Application> requestEntity = new HttpEntity<Application>(application, headers);
        String url = ManagerPart.getUrl()+"/manager/"+orgId+"/process-instances/MsgStartManager";
        ResponseEntity<Application> response = restTemplate.postForEntity(url ,requestEntity , Application.class);
        logger.info(response.getBody().toString());
        return new ResponseEntity<Application>(application , HttpStatus.OK);
    }


    /**
     * *****************from Manager participant****************************************
     */

    /**
     * receive messages from manager and  return response to other participant.
     * M-V
     * @param orgId
     * @param pid
     * @param msgType
     * @param payload
     * @return
     */
    @RequestMapping(value = "/manager/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String managerDispature(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                         @PathVariable("msgType") String msgType, @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : "+msgType+" from manager-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vOrgId = payload.get("vOrgId").toString();
        String vpid = payload.get("vpid").toString();
        pairLRepository.match(vOrgId , vpid , new ManagerPart(orgId , pid));
        logger.info(pairLRepository.getPairs().toString());
        return "V-M match successfully";
    }




}
