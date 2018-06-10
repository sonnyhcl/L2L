package vmc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vmc.domain.*;
import vmc.repos.ManagerRepository;
import vmc.repos.PairRepository;
import vmc.repos.VesselRepository;

import java.util.HashMap;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairLRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private VesselRepository vesselRepository;



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
                                                     @RequestBody Application application) throws JsonProcessingException {

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received application for spare part");
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vid = application.getVid();
        VesselPart vesselPart = vesselRepository.findByOrgId(orgId);
        VesselProcessInstance vpi = new VesselProcessInstance(pid , orgId , vid);
        Pair pair = new Pair(vpi , null);
        pairLRepository.register(pair);

        //TODO:select manager for application

        String mOrgId = "MA1001";
        ManagerPart mpart= managerRepository.findByOrgId(mOrgId);
        //TODO:send MsgStartManager to manager
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String applyId = pid+ CommonUtil.getGuid();
        application.setId(applyId);
        application.setMOrgId(mOrgId);
//        String body = objectMapper.writeValueAsString(application);
        HttpEntity<Application> requestEntity = new HttpEntity<Application>(application, headers);
        String url = mpart.getUrl()+"/manager/"+orgId+"/process-instances/MsgStartManager";
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
        pairLRepository.match(vOrgId , vpid , new ManagerProcessInstance(pid , orgId));
        logger.info(pairLRepository.getPairs().toString());
        return "V-M match successfully";
    }




}
