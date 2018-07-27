package managerA.coordinator;

import com.fasterxml.jackson.core.JsonProcessingException;
import managerA.domain.Application;
import managerA.domain.Location;
import managerA.repos.ApplicationRepository;
import managerA.repos.CommonRepository;
import managerA.repos.LocationRepository;
import managerA.service.RestClient;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CoreController extends AbstractController {
    @Autowired
    private RestClient restClient;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private LocationRepository locationRepository;

    @RequestMapping("/hello")
    public String hello(){
        logger.debug("hello ,manager");
        restClient.test();
        return "hello , manager";
    }


    @RequestMapping(value = "/{orgId}/process-instances/MsgStartManager", method = RequestMethod.POST, produces = "application/json")
    public String startManagerProc(@PathVariable("orgId") String orgId,
                                                        @RequestBody Application application) throws JsonProcessingException {
        logger.info(orgId+" : MsgStartManager");
        logger.info("***********startManagerProc***********");
        //TODO: start manager process instance by message MsgStartManager.
        Map<String , Object>  vars = new HashMap<String , Object>();
        vars.put("orgId" , application.getMOrgId());
        vars.put("applyId", application.getId());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartManager", vars);

        //TODO:save as document
        application.setMpid(pi.getId());
        applicationRepository.save(application);

        //TODO: register manager process instance in collaboration group.
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        sendData.put("vOrgId" , application.getVOrgId());
        sendData.put("vpid" , application.getVpid());
        sendData.put("vid" , application.getVid());
        sendData.put("applyId" ,application.getId());
        String url = commonRepository.getVmcContextPath()+"/manager/"+orgId+"/"+pi.getId()+"/match";
        String rep = restClient.matchVessel(url, sendData);
        logger.info(rep);
        return "Start manager successfully";
    }

    @RequestMapping(value = "/location", method = RequestMethod.GET)
    public ResponseEntity<Location> getLocationByName(@RequestParam(value = "name") String name){
        Location location = locationRepository.findByName(name.trim());
        return new ResponseEntity<Location>(location , HttpStatus.OK);
    }

}
