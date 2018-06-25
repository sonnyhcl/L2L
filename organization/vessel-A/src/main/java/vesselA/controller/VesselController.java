package vesselA.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.app.model.runtime.ProcessInstanceRepresentation;
import org.activiti.app.service.api.UserCache;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vesselA.repos.CommonRepository;
import vesselA.repos.ShadowRepository;
import vesselA.util.DateUtil;
import vesselA.domain.Application;
import vesselA.domain.Destination;
import vesselA.domain.VesselShadow;
import vesselA.util.MapUtil;

import java.text.DecimalFormat;
import java.util.*;

/**
 * business locgic for interaction between  Monitor and Vessel
 * @author bqzhu
 */
@RestController
public class VesselController extends AbstractController {

    private static  Logger logger = LoggerFactory.getLogger(VesselController.class);


    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ShadowRepository shadowRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @RequestMapping("/hello")
    String hello(){
        logger.info("hello , girls");
        return "hello , girls";
    }
    /**
     * start a process instance by process name.
     * @param mp
     * @param processName
     * @return
     */
    @RequestMapping(value = "/process-instances/{processName}", method = RequestMethod.POST)
    public ProcessInstanceRepresentation StartProcessInstanceByName(@RequestBody Map<String, Object> mp , @PathVariable("processName") String processName) {
        logger.info("--POST /process-instances/"+processName+"--");
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionName(processName).latestVersion().singleResult();
        logger.info(processDefinition.toString());
        Map<String , Object> vars = new HashMap<String , Object>();
        String sailor = mp.get("sailor").toString();
        String vid = mp.get("vid").toString();
        int defaultDelayHour = Integer.parseInt(mp.get("defaultDelayHour").toString());
        int zoomInVal = Integer.parseInt(mp.get("zoomInVal").toString());
        String orgId = commonRepository.getOrgId();
        vars.put("orgId" , orgId);
        vars.put("vid" , vid);
        vars.put("sailor" , sailor);

        VesselShadow vs = new VesselShadow();
        vs.setId(vid);
        vs.setDefaultDelayHour(defaultDelayHour);
        vs.setZoomInVal(zoomInVal);
        //TODO: create shadow for process instance.
        shadowRepository.save(vs);

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId() , vars);

        HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery().processInstanceId
                (processInstance.getId()).singleResult();

        User user = null;
        if (historicProcess.getStartUserId() != null) {
            UserCache.CachedUser cachedUser = userCache.getUser(historicProcess.getStartUserId());
            if (cachedUser != null && cachedUser.getUser() != null) {
                user = cachedUser.getUser();
            }
        }
        return new ProcessInstanceRepresentation(historicProcess, processDefinition, ((ProcessDefinitionEntity)
                processDefinition).isGraphicalNotationDefined(), user);
    }


    /******************************REST API for operations on vessel cache **************************/

    /**
     * Custom GET/PUT Variables
     */
    @RequestMapping(value = "/vessel/shadow/{pid}", method = RequestMethod.GET)
    public ResponseEntity<VesselShadow> queryVesselShadow(@PathVariable String pid) {
        VesselShadow result = null;
        Map<String , Object> vars = runtimeService.getVariables(pid);
        String vid = vars.get("vid").toString();
        result = shadowRepository.findById(vid);

        return new ResponseEntity<VesselShadow>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vessel/shadow/{pid}", method = RequestMethod.PUT)
    public ResponseEntity<VesselShadow> updateVesselShadow(@PathVariable String pid , @RequestBody VesselShadow vesselShadow) {
        Map<String , Object> vars = runtimeService.getVariables(pid);
        String vId = vars.get("vid").toString();
        shadowRepository.update(vesselShadow);
        return new ResponseEntity<VesselShadow>(vesselShadow, HttpStatus.OK);
    }


    @RequestMapping(value = "/{pid}/apply", method = RequestMethod.POST)
    public ResponseEntity<Application> apply(@PathVariable String pid , @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        logger.info("---GET /{pid}/apply---"+payload.toString());
        VesselShadow result = null;
        //TODO:Forward request to vmc
        Map<String, Object> pvars = runtimeService.getVariables(pid);
        String vid = pvars.get("vid").toString();
        String vOrgId = pvars.get("orgId").toString();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        HashMap<String , Object> sendData = new HashMap<String , Object>();

        String spName = payload.get("spName").toString();
        int spNumber= Integer.parseInt(payload.get("spNumber").toString());

        //TODO:Map Destination List  to String List.
        List<Destination> destinations = vesselShadow.getRemainingDestinations();
        List<String> candidateDestinations = MapUtil.Destinations2Dnames(destinations);
        String status = vesselShadow.getStatus();
        int nextPortIndex = vesselShadow.getNextPortIndex();

        long startMs = DateUtil.str2date(vesselShadow.getSimuStartTime()).getTime();
        long nowMs= new Date().getTime();
        String timeStamp = DateUtil.date2str(DateUtil.transForDate(nowMs + (nowMs-startMs)*vesselShadow.getZoomInVal()));
        Application application = new Application(null , vOrgId ,pid , vid , spName , spNumber , candidateDestinations , timeStamp);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Application> requestEntity = new HttpEntity<Application>(application, headers);
        String url = commonRepository.getVmcContextPath()+"/vessel/"+vOrgId+"/"+pid+"/apply";
        ResponseEntity<Application> response = restTemplate.postForEntity(url ,requestEntity , Application.class);
        logger.info(response.getBody().toString());
        return new ResponseEntity<Application>(application, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/ADTask/info" , method = RequestMethod.GET)
    HashMap<String , Object> getADTaskInfo(@PathVariable String pid){
        logger.debug("--GET /{pid}/ADTask/info--"+pid);
        HashMap<String,Object>  info  = new HashMap<String,Object>();
        Map<String, Object> pvars = runtimeService.getVariables(pid);
        String vid = pvars.get("vid").toString();
        VesselShadow vesselShadow = shadowRepository.findById(vid);
        List<Destination> dests = vesselShadow.getDestinations();
        Destination curDest = dests.get(vesselShadow.getNextPortIndex()-1);
        String status = vesselShadow.getStatus();
        logger.debug(status);
        //TODO:port name
        info.put("pname" , curDest.getName());
        //TODO: status
        info.put("status" , status);

        //TODO: current time
        String simuStartTime = vesselShadow.getSimuStartTime();
        Date curDate = new Date();
        long simuStartMs = DateUtil.str2date(simuStartTime).getTime();
        simuStartMs += (curDate.getTime() - simuStartMs)*vesselShadow.getZoomInVal();
        String currentTime = DateUtil.date2str(DateUtil.transForDate(simuStartMs));
        info.put("curTime" , currentTime);
        if(status.equals("Anchoring")){
            //TODO:  elapse time of arrival
            double arrivalElapseHour = DateUtil.TimeMinus(curDest.getEstiArrivalTime() , currentTime)*1.0/(1000*60*60);
            DecimalFormat df = new DecimalFormat("0.###");
            String arrivalElapseTime = df.format(arrivalElapseHour);
            info.put("arrivalElapseTime" , arrivalElapseTime);
            //TODO: anchoring time
            info.put("anchoringTime" , curDest.getEstiAnchorTime());
            //TODO: estimate arrival time
            info.put("arrivalTime" , curDest.getEstiArrivalTime());
        }else if(status.equals("Docking")){
            //TODO:  elapse time of despature
            double departureElapseHour = DateUtil.TimeMinus(curDest.getEstiDepartureTime(), currentTime)*1.0/(1000*60*60);
            DecimalFormat df = new DecimalFormat("0.###");
            String departureElapseTime = df.format(departureElapseHour);
            info.put("departureElapseTime" , departureElapseTime);
            //TODO: arrival time
            info.put("arrivalTime" ,curDest.getEstiArrivalTime());
            //TODO: departure time
            info.put("departureTime" , curDest.getEstiDepartureTime());
        }else{
            logger.debug("Error status.");
        }
        return  info;
    }

    @RequestMapping(value = "/{pid}/shadow/{status}" , method = RequestMethod.POST , produces = "application/json")
    public ResponseEntity<String> updateStatus(@PathVariable("pid") String pid , @PathVariable("status") String status){
        logger.debug("--status--"+status);
        VesselShadow vesselShadow = shadowRepository.findByPid(pid);
        vesselShadow.setStatus(status);
        return new ResponseEntity<String>(status , HttpStatus.OK);
    }
}
