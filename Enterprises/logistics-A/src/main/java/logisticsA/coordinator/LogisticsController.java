package logisticsA.coordinator;

import com.fasterxml.jackson.core.JsonProcessingException;
import logisticsA.domain.*;
import logisticsA.repos.*;
import logisticsA.service.RestClient;
import logisticsA.websocket.StompClient;
import logisticsA.util.CommonUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@SuppressWarnings("all")
public class LogisticsController extends AbstractController {
    private final Logger logger = LoggerFactory.getLogger(LogisticsController.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private StompClient stompClient;

    @Autowired
    private LogisticsRepository logisticsRepository;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private RoutePlanRepository routePlanRepository;


    @Autowired
    private LocationRepository locationRepository;

    @RequestMapping("/hello")
    public String hello() {
        logger.debug("hello ,vesselA");
        return "hello , vesselA";
    }

    /**
     * receive message StartLogistic from SLC.
     *
     * @param orgId
     * @param msgType
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/{orgId}/process-instances/MsgStartLogistic", method = RequestMethod.POST, produces = "application/json")
    public String startLogisticProc(@PathVariable("orgId") String orgId,
                                    @RequestBody Logistics logistics) throws JsonProcessingException {
        logger.info("***********startLogisticProc***********");
        logger.info(orgId + " : MsgStartLogistic");
        //TODO: save to logistics repository
        logisticsRepository.save(logistics);

        //TODO: start Logistics process instance by message MsgStartLogistic.
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("orgId", logistics.getLOrgId());
        vars.put("logisticId", logistics.getId());
        vars.put("wid", logistics.getWid());
        ProcessInstance pi = runtimeService.startProcessInstanceByMessage("MsgStartLogistic", vars);

        return "Start logistics successfully.";
    }

    @RequestMapping(value = "/{lpid}/logistics", method = RequestMethod.GET)
    public ResponseEntity<Logistics> queryLogisticByLPid(@PathVariable("lpid") String lpid) {
        logger.info("--GET /{pid}/logistics---" + lpid);
        Logistics result = null;
        result = logisticsRepository.findByLpid(lpid);
        return new ResponseEntity<Logistics>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/logistics", method = RequestMethod.POST)
    public ResponseEntity<Logistics> updateLogistic(@PathVariable("pid") String pid, @RequestBody Logistics logistics) {
        logger.info("--POST /{pid}/logistics---" + pid);
        Logistics result = null;
        result = logisticsRepository.update(logistics);
        return new ResponseEntity<Logistics>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/freights", method = RequestMethod.GET)
    public ResponseEntity<List<Freight>> queryVesselShadow() {
        logger.info("--GET /freights---");
        List<Freight> result = null;

        result = freightRepository.getFreights();

        return new ResponseEntity<List<Freight>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow/status/{status}", method = RequestMethod.POST, produces = "application/json") // status --- msgType
    public ResponseEntity<String> updateStatus(@PathVariable("pid") String pid, @PathVariable("status") String status, @RequestBody HashMap<String, Object> mp) {
        logger.debug("--POST /{pid}/shadow/status/{status}--" + status);
        Logistics logistics = logisticsRepository.findByLpid(pid);
        String policy = logistics.getCategory();
        logistics.setStatus(status);
        runtimeService.setVariable(pid , "processStatus" , status);
        if (status.equals("Planning") || status.equals("Meeting") || status.equals("Missing")) {
            if (policy.equals("variable-destination")) {
                if (status.equals("Planning")) {
                    RoutePlan routePlan = new RoutePlan();
                    String planId = "WP" + pid + CommonUtil.getGuid();
                    routePlan.setId(planId);// wagon plan id;
                    routePlan.setLpid(pid);
                    routePlan.setMsgType(status);
                    routePlan.setMsgBody(mp);
                    routePlanRepository.save(routePlan);
                    runtimeService.setVariable(pid, "planId", planId);
                    logger.debug("Planning --- " + routePlan.toString());
                }
                // TODO : complete the Running User Task
                Task task = taskService.createTaskQuery().processInstanceId(pid).taskName("Running").singleResult();
                taskService.complete(task.getId());
                logger.debug("Complete Running Task. pid = " + pid);
                if (status.equals("Missing")) {//After collaboration is established ，missing
                    stompClient.sendPlanMissingMsg("admin", "/topic/route/missing", pid, policy, "Missing delivery opportunity", routePlanRepository.getC2(pid), -1, -1, false);
                }
            } else if (policy.equals("fixed-destination")) {
                if(status.equals("Planning")){
                    logger.debug("Don't allow to re-plan  rendezvous  according to poilcy : fixed-destination");
                }
                if(status.equals("Missing")){
                    RoutePlan routePlan = routePlanRepository.getLatestPlan(pid);
                    double totalCost = routePlan.getRendezvous().getSumCost();
                    double riskCost = totalCost *0.42;
                    stompClient.sendPlanMissingMsg("admin", "/topic/route/missing", pid, policy, "Missing delivery opportunity", totalCost , riskCost);
                    Task task = taskService.createTaskQuery().processInstanceId(pid).taskName("Running").singleResult();
                    taskService.complete(task.getId());
                    logger.debug("Complete Running Task. pid = " + pid);
                }
            } else {
                logger.debug("unsuppoted policy.");
            }
        }
        return new ResponseEntity<String>("{\"status\": \"OK\"}", HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow", method = RequestMethod.POST)
    ResponseEntity<WagonShadow> updateShadow(@PathVariable("pid") String pid, @RequestBody HashMap<String, Object> mp) throws JsonProcessingException {
//        logger.debug("--POST /{pid}/shadow--" + mp.toString());
        double longitude = Double.parseDouble(mp.get("longitude").toString());
        double latitude = Double.parseDouble(mp.get("latitude").toString());
        double speed = Double.parseDouble(mp.get("speed").toString());
        double movedDistance = Double.parseDouble(mp.get("movedDistance").toString());
        double deltaNavDist = Double.parseDouble(mp.get("deltaNavDist").toString());

        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        Rendezvous r = wagonShadow.getRendezvous();
        double deltaNavCost = -1;
        if (r == null) {
            logger.debug("目的港口不能为空");
        } else {
            Logistics logistics = logisticsRepository.findByLpid(pid);
            String policy = logistics.getCategory();

            String pname = r.getName();
            double f = freightRepository.findByName(pname);
            if(policy.equals("fixed-destination")){ // freight discount fifty percent.
                    f = f * 0.5;
            }
            deltaNavCost = f * deltaNavDist * logistics.getSpWight();
        }

        wagonShadow.setLongitude(longitude);
        wagonShadow.setLatitude(latitude);
        wagonShadow.setSpeed(speed);
        wagonShadow.setMovedDistance(movedDistance);
        wagonShadow.setDeltaNavCost(deltaNavCost);

        return new ResponseEntity<WagonShadow>(wagonShadow, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow", method = RequestMethod.GET)
    ResponseEntity<WagonShadow> getShadow(@PathVariable("pid") String pid) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        return new ResponseEntity<WagonShadow>(wagonShadow, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/shadow/currentCost", method = RequestMethod.GET)
    ResponseEntity<Double> getCurrentCost(@PathVariable("pid") String pid) throws JsonProcessingException {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        double currentCost = wagonShadow.getLastNavsCost() + wagonShadow.getDeltaNavCost();
        return new ResponseEntity<Double>(currentCost, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/arrival", method = RequestMethod.POST)
    ResponseEntity<WagonShadow> arrival(@PathVariable("pid") String pid, @RequestBody HashMap<String, Object> body) throws JsonProcessingException {
        logger.debug("--POST /{pid}/arrival--");
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
        wagonShadow.setStatus("Arrival");
        return new ResponseEntity<WagonShadow>(wagonShadow, HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/traffic", method = RequestMethod.POST)
    ResponseEntity<String> traffic(@PathVariable("pid") String pid, @RequestBody HashMap<String, Object> body) throws JsonProcessingException {
        logger.debug("--/{pid}/traffic--");
        Logistics logistics = logisticsRepository.findByLpid(pid);
        String policy = logistics.getCategory();
        if(policy.equals("variable-destination")){
            logistics.setStatus("Planning");
            runtimeService.setVariable(pid , "processStatus" , "Planning");
        }else if(policy.equals("fixed-destination")){
            logger.debug("Don't allow to re-plan  rendezvous  according to poilcy : fixed-destination");
        }else{
            logger.debug("unsuppoted policy.");
        }
        return new ResponseEntity<String>("{\"status\": \"OK\"}", HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/wagon/status", method = RequestMethod.GET)
    ResponseEntity<String> checkStatus(@PathVariable("pid") String pid) {
        WagonShadow wagonShadow = wagonShadowRepository.findByPid(pid);
//        String status = "Running";
//        Location rendLoc = locationRepository.findByName(wagonShadow.getRendezvous().getName());
//        logger.debug(rendLoc+"--"+wagonShadow.getLatitude()+" , "+wagonShadow.getLatitude());
//        if (rendLoc.getLongitude() == wagonShadow.getLongitude() && rendLoc.getLatitude() == wagonShadow.getLatitude()) {
//            status = "Arrival";
//        }
        return new ResponseEntity<String>(wagonShadow.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{pid}/process/status" , method = RequestMethod.GET , produces = "application/json")
    public ResponseEntity<String> getProcessStatus(@PathVariable("pid") String pid ){
        String processStatus = runtimeService.getVariable(pid , "processStatus").toString();
        logger.debug("/{pid}/process/status--"+processStatus);
        String payload= "{\"processStatus\":\""+processStatus+"\"}";
        return  new ResponseEntity<String>(payload ,  HttpStatus.OK);
    }
}
