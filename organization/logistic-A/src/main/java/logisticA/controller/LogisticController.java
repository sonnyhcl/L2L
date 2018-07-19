package logisticA.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logisticA.domain.*;
import logisticA.repos.FreightRepository;
import logisticA.repos.LogisticRepository;
import logisticA.repos.RoutePlanRepository;
import logisticA.repos.WagonShadowRepository;
import logisticA.service.RestClient;
import logisticA.util.CommonUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@SuppressWarnings("all")
public class LogisticController extends AbstractController {
    private  final Logger logger = LoggerFactory.getLogger(LogisticController.class);

    @Autowired
    private RestClient restClient;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private RoutePlanRepository routePlanRepository;

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/hello")
    public String hello(){
        logger.debug("hello ,logisticA");
        return "hello , logisticA";
    }

    /**
     * receive message StartLogistic from SLC.
     * @param orgId
     * @param msgType
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/{orgId}/process-instances/MsgStartLogistic", method = RequestMethod.POST, produces = "application/json")
    public String startLogisticProc(@PathVariable("orgId") String orgId,
                                                    @RequestBody Logistic logistic) throws JsonProcessingException {
        logger.info("***********startLogisticProc***********");
        logger.info(orgId+" : MsgStartLogistic");
        //TODO: save to logistic repository
        logisticRepository.save(logistic);

        //TODO: start Logistic process instance by message MsgStartLogistic.
        Map<String , Object> vars = new HashMap<String , Object>();
        vars.put("orgId" , logistic.getLOrgId());
        vars.put("logisticId" , logistic.getId());
        vars.put("wid" , logistic.getWid());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartLogistic", vars);

        return "Start logistic successfully.";
    }

    @RequestMapping(value = "/{lpid}/logistic", method = RequestMethod.GET)
    public ResponseEntity<Logistic> queryLogisticByLPid(@PathVariable("lpid") String lpid) {
        logger.info("--GET /{pid}/logistic---"+lpid);
        Logistic result = null;
        result = logisticRepository.findByLpid(lpid);
        return new ResponseEntity<Logistic>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/logistic", method = RequestMethod.POST)
    public ResponseEntity<Logistic> updateLogistic(@PathVariable("pid") String pid , @RequestBody Logistic logistic) {
        logger.info("--POST /{pid}/logistic---"+pid);
        Logistic result = null;
        result = logisticRepository.update(logistic);
        return new ResponseEntity<Logistic>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/freights", method = RequestMethod.GET)
    public ResponseEntity<List<Freight>> queryVesselShadow() {
        logger.info("--GET /freights---");
        List<Freight> result = null;

        result = freightRepository.getFreights();

        return new ResponseEntity<List<Freight>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow/status/{status}" , method = RequestMethod.POST , produces = "application/json")// status --- msgType
    public ResponseEntity<String> updateStatus(@PathVariable("pid") String pid , @PathVariable("status") String status , @RequestBody HashMap<String , Object> mp){
        logger.debug("--POST /{pid}/shadow/status/{status}--"+status);
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setStatus(status);
        if(status.equals("Planning") ||status.equals("Meeting")||status.equals("Missing")){
            if(status.equals("Planning")){
                RoutePlan routePlan = new RoutePlan();
                String planId = "WP"+pid+CommonUtil.getGuid();
                routePlan.setId(planId);// wagon plan id;
                routePlan.setLpid(pid);
                routePlan.setMsgType(status);
                routePlan.setMsgBody(mp);
                routePlanRepository.save(routePlan);
                runtimeService.setVariable(pid  , "planId" , planId);
                logger.debug("Planning --- "+routePlan.toString());
            }
            if(status.equals("Missing")){//协作建立后，出现Ｍissing
                ObjectNode payload = objectMapper.createObjectNode();
                payload = objectMapper.createObjectNode();
                payload.put("from" , pid);
                payload.put("reason" , "车船错过交货机会");
                payload.put("C0" , routePlanRepository.getC2(pid));
                payload.put("C1" , -1);//这里使用第二次，不改变目的港口，代表不对变化做出决策，即因变化二需要的新成本
                payload.put("C2" , -1);
                payload.put("isFirst" , false);
                simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route/missing" , payload);
            }
            Task task = taskService.createTaskQuery().processInstanceId(pid).taskName("Running").singleResult();
            taskService.complete(task.getId());
            logger.debug("Complete Running Task. pid = "+pid);
        }
        return new ResponseEntity<String>(status , HttpStatus.OK);
    }
    @RequestMapping(value = "/{pid}/shadow" , method = RequestMethod.POST)
    ResponseEntity<WagonShadow> updateShadow(@PathVariable("pid") String pid,@RequestBody HashMap<String , Object> mp) throws JsonProcessingException {
        logger.debug("--POST /{pid}/shadow--"+mp.toString());
        double longitude = Double.parseDouble(mp.get("longitude").toString());
        double latitude = Double.parseDouble(mp.get("latitude").toString());
        double speed = Double.parseDouble(mp.get("speed").toString());
        double movedDistance = Double.parseDouble(mp.get("movedDistance").toString());
        double deltaNavDist = Double.parseDouble(mp.get("deltaNavDist").toString());

        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        Rendezvous r = wagonShadow.getRendezvous();
        double deltaNavCost = -1;
        if(r == null){
            logger.debug("目的港口不能为空");
        }else{
            String pname = r.getName();
            double f = freightRepository.findByName(pname);
            Logistic logistic = logisticRepository.findByLpid(pid);
            deltaNavCost = f*deltaNavDist*logistic.getSpWight();
        }

        wagonShadow.setLongitude(longitude);
        wagonShadow.setLatitude(latitude);
        wagonShadow.setSpeed(speed);
        wagonShadow.setMovedDistance(movedDistance);
        wagonShadow.setDeltaNavCost(deltaNavCost);

        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
    }
    @RequestMapping(value = "/{pid}/shadow" , method = RequestMethod.GET)
    ResponseEntity<WagonShadow> getShadow(@PathVariable("pid") String pid ) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow/currentCost" , method = RequestMethod.GET)
    ResponseEntity<Double> getCurrentCost(@PathVariable("pid") String pid ) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        double currentCost = wagonShadow.getLastNavsCost()+wagonShadow.getDeltaNavCost();
        return new ResponseEntity<Double>(currentCost, HttpStatus.OK);
    }
//    @RequestMapping(value = "/{pid}/arrival" , method = RequestMethod.POST)
//    ResponseEntity<WagonShadow> arrival(@PathVariable("pid") String pid , @RequestBody HashMap<String , Object> body) throws JsonProcessingException {
//        logger.debug("--POST /{pid}/arrival--");
//        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
//        wagonShadow.setStatus("Meeting");
//        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
//    }

    @RequestMapping(value = "/{pid}/traffic" , method = RequestMethod.POST)
    ResponseEntity<WagonShadow> traffic(@PathVariable("pid") String pid , @RequestBody HashMap<String , Object> body) throws JsonProcessingException {
        logger.debug("--/{pid}/traffic--");
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setStatus("Planning");
        return new ResponseEntity<WagonShadow>(wagonShadow , HttpStatus.OK);
    }

}
