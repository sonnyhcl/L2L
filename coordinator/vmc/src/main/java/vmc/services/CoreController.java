package vmc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vmc.domain.*;
import vmc.repos.ManagerRepository;
import vmc.repos.PairRepository;
import vmc.repos.VesselRepository;

import java.util.HashMap;

@RestController
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestClient restClient;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private VesselRepository vesselRepository;
    @RequestMapping(value = "/{testId}/hello",method = RequestMethod.POST)
    public  String hello(@PathVariable("testId") String testId ,  @RequestBody HashMap<String, Object> payload ){
        logger.debug("hello ,vmc--"+testId);
        return "hello , vmc";
    }

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
        logger.debug("--POST /vessel/{orgId}/{pid}/apply-- : "+application.toString());
        //TODO: genarete applyId
        String applyId = pid+ CommonUtil.getGuid();
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vid = application.getVid();
        VesselPart vesselPart = vesselRepository.findByOrgId(orgId);
        VesselProcessInstance vpi = new VesselProcessInstance(pid , orgId , vid);
        if(pairRepository.isRegistried(orgId , pid) == false){
            Pair pair = new Pair(vpi , null);
            pairRepository.register(pair);
        }


        //TODO:select manager for application

        String mOrgId = "MA1001";
        ManagerPart mpart= managerRepository.findByOrgId(mOrgId);
        //TODO:send MsgStartManager to manager
        application.setId(applyId);
        application.setMOrgId(mOrgId);
        String url = mpart.getUrl()+"/api/"+orgId+"/process-instances/MsgStartManager";
        restClient.startManager(url , application , vesselPart);
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
    @RequestMapping(value = "/manager/{orgId}/{pid}/{msgType}", method = RequestMethod.POST)
    public String managerDispature(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                         @PathVariable("msgType") String msgType, @RequestBody HashMap<String, Object> payload) {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : "+msgType+" from manager-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vOrgId = payload.get("vOrgId").toString();
        String vpid = payload.get("vpid").toString();
        String applyId = payload.get("applyId").toString();
        pairRepository.match(vOrgId , vpid , new ManagerProcessInstance(pid , orgId ,applyId));
        logger.info(pairRepository.getPairs().toString());
        return "V-M match successfully";
    }




}
