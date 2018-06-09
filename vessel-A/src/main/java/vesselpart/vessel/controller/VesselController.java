package vesselpart.vessel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.app.model.runtime.ProcessInstanceRepresentation;
import org.activiti.app.service.api.UserCache;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vesselpart.cache.VesselCache;
import vesselpart.util.CommonUtil;
import vesselpart.util.DateUtil;
import vesselpart.vessel.domain.Application;
import vesselpart.vessel.domain.Port;
import vesselpart.vessel.domain.VesselShadow;

import java.util.*;

/**
 * business locgic for interaction between  Monitor and Vessel
 * @author bqzhu
 */
@RestController
public class VesselController extends AbstractController {

    @Autowired
    private VesselCache vesselCache;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * start a process instance by process name.
     * @param mp
     * @param processName
     * @return
     */
    @RequestMapping(value = "/vessel/process-instances/{processName}", method = RequestMethod.POST)
    public ProcessInstanceRepresentation StartProcessInstanceByName(@RequestBody Map<String, Object> mp , @PathVariable("processName") String processName) {
        System.out.println(mp.toString());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionName(processName).latestVersion().singleResult();
        System.out.println(processDefinition.toString()+processDefinition.getId()+runtimeService);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId() , mp);

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
        String vId = vars.get("vid").toString();
        result = vesselCache.getVesselShadows().get(vId);
        if(result.getPositionIndex() >= 309){
            logger.debug("vessel shadow : "+result.toString());
        }

        return new ResponseEntity<VesselShadow>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/vessel/shadow/{pid}", method = RequestMethod.PUT)
    public ResponseEntity<VesselShadow> updateVesselShadow(@PathVariable String pid , @RequestBody VesselShadow vesselShadow) {
        Map<String , Object> vars = runtimeService.getVariables(pid);
        String vId = vars.get("vid").toString();
        vesselCache.getVesselShadows().put(vId , vesselShadow);
        return new ResponseEntity<VesselShadow>(vesselShadow, HttpStatus.OK);
    }


    @RequestMapping(value = "/vessel/{pid}/apply", method = RequestMethod.GET)
    public ResponseEntity<Application> apply(@PathVariable String pid , @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        VesselShadow result = null;
        //TODO:Forward request to vmc
        Map<String, Object> pvars = runtimeService.getVariables(pid);
        String vid = pvars.get("vid").toString();
        String vOrgId = pvars.get("orgId").toString();
        VesselShadow vesselShadow = vesselCache.getVesselShadows().get(vid);
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        String applyId = pid+ CommonUtil.getGuid();
        String spName = payload.get("spName").toString();
        int spNumber= Integer.parseInt(payload.get("spNumber").toString());

        List<String> candidatePorts = new ArrayList<String>();
        List<Port> ports = vesselShadow.getPorts();
        String status = vesselShadow.getStatus();
        int nextPortIndex = vesselShadow.getNextPortIndex();
        for(int i = 0 ; i < ports.size(); i++){
            if(i >= nextPortIndex){
                Port p = ports.get(i);
                if(i == nextPortIndex) {
                    if ("Anchoring".equals(status) || "Docking".equals(status)) {
                        candidatePorts.add(ports.get(nextPortIndex - 1).getPname());
                    }
                }
                candidatePorts.add(p.getPname());
            }
        }
        long startMs = DateUtil.str2date(vesselShadow.getSimuStartDateStr()).getTime();
        long nowMs= new Date().getTime();
        String timeStamp = DateUtil.date2str(DateUtil.transForDate(nowMs + (nowMs-startMs)*vesselCache.getZoomVal()));
        Application application = new Application(applyId , vOrgId ,pid , vid , spName , spNumber , candidatePorts , timeStamp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        String body = objectMapper.writeValueAsString(application);
        HttpEntity<Application> requestEntity = new HttpEntity<Application>(application, headers);
        String url = vesselCache.getCoBasePath()+"/vessel/"+vOrgId+"/process-instances/MsgStartManager";
        ResponseEntity<Application> response = restTemplate.postForEntity(url ,requestEntity , Application.class);
        logger.info(response.getBody().toString());
        return new ResponseEntity<Application>(application, HttpStatus.OK);
    }

}
