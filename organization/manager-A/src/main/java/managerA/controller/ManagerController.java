package managerA.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import managerA.repos.ApplicationRepository;
import managerA.repos.CommonRepository;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import managerA.domain.Application;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ManagerController extends AbstractController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;



    @RequestMapping(value = "/manager/{orgId}/process-instances/MsgStartManager", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Application> startManagerProc(@PathVariable("orgId") String orgId,
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String body = objectMapper.writeValueAsString(sendData);
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = commonRepository.getVmcContextPath()+"/manager/"+orgId+"/"+pi.getId()+"/match";
        ResponseEntity<String> response = restTemplate.postForEntity(url , requestEntity , String.class);
        logger.info(response.getBody());

        return new ResponseEntity<Application>(application, HttpStatus.OK);
    }

}
