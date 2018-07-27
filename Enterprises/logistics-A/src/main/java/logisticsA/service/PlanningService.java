package logisticsA.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import logisticsA.domain.*;
import logisticsA.repos.*;
import logisticsA.util.PathUtil;
import logisticsA.websocket.StompClient;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Service("planningService")
@SuppressWarnings("all")
public class PlanningService implements JavaDelegate, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PlanningService.class);

    private static final String topic = "activiti/wagon/navigate";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private LogisticsRepository logisticsRepository;

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    public StompClient stompClient;

    @Autowired
    private RoutePlanRepository routePlanRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String pid = delegateExecution.getProcessInstanceId();
        logger.info("************************PlanningService************************" + pid);

        //TODO:get process variables
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String logisticId = pvars.get("logisticId").toString();
        String orgId = pvars.get("orgId").toString();
        String wid = pvars.get("wid").toString();
        String planId = pvars.get("planId").toString();
        String status = pvars.get("processStatus").toString();

        //TODO: get logistics.
        Logistics logistics = logisticsRepository.findById(logisticId);

        String policy = logistics.getCategory();
        if (policy.equals("variable-destination")) {
            onVariableDestination(logistics, status, wid, planId, orgId, pid);
        } else if (policy.equals("fixed-destination")) {
            onFixedDestination(logistics, status, wid, planId, orgId, pid);
        } else {
            logger.debug("unsuppoted policy.");
        }

    }

    private void onFixedDestination(Logistics logistics, String status, String wid, String planId, String orgId, String pid) {
        logger.debug("--onFixedDestination--");
        WagonShadow wagonShadow = wagonShadowRepository.findById(wid);
        List<String> destinations = logistics.getDestinations();
        //TODO: 获取当前规划对象
        RoutePlan routePlan = routePlanRepository.findById(planId);
        planPath(routePlan, destinations, wagonShadow);
        //TODO : decide rendezvous
        String url = commonRepository.getLvcContextPath() + "/logistics/" + orgId + "/" + pid + "/route/decide";
        RoutePlan decidedRoutePlan = restClient.decide(url, routePlan);
        routePlan.setRendezvousList(decidedRoutePlan.getRendezvousList());
        routePlan.setRendezvous(decidedRoutePlan.getRendezvous());
        String destName = routePlan.getRendezvous().getName();
        logger.debug("destination : " + routePlan.getRendezvous().getName());
        wagonShadow.setRendezvous(routePlan.getRendezvous());

        switch (destName) {
            case "MISSING":
                logger.debug("Missing."); //Initial plan ,   missing delivery chance.
                RoutePlan routePlan1 = routePlanRepository.getLatestPlan(pid);
                logger.debug(routePlan1.getRendezvous().toString());
                stompClient.sendPlanMissingMsg("admin", "/topic/route/missing", pid, logistics.getCategory(), "Missing delivery opportunity", 0, 0);
                logistics.setStatus("Missing");
                break;
            case "FAIL":
                logger.debug("Fail");
                stompClient.sendPlanFailMsg("admin", "/topic/route/fail", pid, logistics.getCategory(), "No rendezvous found.");
                //TODO :  temporaly ignore this situation.
                break;
            default:
                //TODO: send route info to navigator for displaying track and simulating running , navigator represents the device side.
                stompClient.sendPlanSuccessMsg("admin", "/topic/route/success", pid, logistics.getCategory(), "Initial  planning", routePlan.getRendezvous() , routePlan.getRendezvous().getSumCost(), 0.0);
                break;
        }
        //TODO: update wagon shadow
        wagonShadow.setStatus("Running");
        runtimeService.setVariable(pid  , "processStatus" , "Running");
    }

    private void onVariableDestination(Logistics logistics, String status, String wid, String planId, String orgId, String pid) {
        logger.debug("--onVariableDestination--");
        //TODO:  if  not  the first  time to plan , stop wagon , than re-plan.
        if (!status.equals("Initiating")) {
            stompClient.sendCommand("admin", "/topic/wagon/pause", "PAUSE", wid);
        }
        WagonShadow wagonShadow = wagonShadowRepository.findById(wid);
        List<String> destinations = logistics.getDestinations();
        //TODO: 获取当前规划对象
        RoutePlan routePlan = routePlanRepository.findById(planId);
        planPath(routePlan, destinations, wagonShadow);
        //TODO : decide rendezvous
        String url = commonRepository.getLvcContextPath() + "/logistics/" + orgId + "/" + pid + "/route/decide";
        RoutePlan decidedRoutePlan = restClient.decide(url, routePlan);
        routePlan.setRendezvousList(decidedRoutePlan.getRendezvousList());
        routePlan.setRendezvous(decidedRoutePlan.getRendezvous());
        wagonShadow.setRendezvous(routePlan.getRendezvous());
        String destName = routePlan.getRendezvous().getName();
        logger.debug("destination : " + destName);
        boolean isFirst = routePlanRepository.getEventType(pid).equals("INITIATING");
        switch (destName) {
            case "MISSING":
                logger.debug("Missing.");
                //Initial plan , find missed delivery.
                stompClient.sendPlanMissingMsg("admin", "/topic/route/missing", pid, logistics.getCategory(), "Missing delivery opportunity", routePlanRepository.getC2(pid), -1, -1, isFirst);
                wagonShadow.setStatus("MISSING");
                break;
            case "FAIL":
                stompClient.sendPlanFailMsg("admin", "/topic/route/fail", pid, logistics.getCategory(), "FAIL", routePlanRepository.getReason(pid), routePlanRepository.getC0(pid),
                        routePlanRepository.getC1(pid), routePlanRepository.getC2(pid), isFirst);
                break;
            default:
                //TODO: send route info to navigator for displaying track and simulating running , navigator represents the device side.
                isFirst = routePlanRepository.getEventType(pid).equals("INITIATING");
                wagonShadow.setLastNavsCost(wagonShadow.getLastNavsCost() + wagonShadow.getDeltaNavCost());
                stompClient.sendPlanSuccessMsg("admin", "/topic/route/success", pid, logistics.getCategory(), "SUCCESS", routePlanRepository.getReason(pid), routePlan.getRendezvous(), routePlanRepository.getC0(pid),
                        routePlanRepository.getC1(pid), routePlanRepository.getC2(pid), isFirst);
                wagonShadow.setRendezvous(routePlan.getRendezvous());//规划成功才设置会合港口
                break;
        }
        runtimeService.setVariable(pid, "status", "Running");

        //TODO: update wagon shadow
        wagonShadow.setStatus("Running");
        runtimeService.setVariable(pid  , "processStatus" , "Running");
    }
    private void planPath(RoutePlan routePlan, List<String> destinations, WagonShadow wagonShadow) {
        logger.debug("route plan size : "+destinations.size());
        for (int i = 0; i < destinations.size(); i++) {
            logger.debug("route plan... " + i);
            Location destination = locationRepository.findByName(destinations.get(i));
            String pathInfo = mapRepository.PlanPath("" + wagonShadow.getLongitude(), "" + wagonShadow.getLatitude()
                    , "" + destination.getLongitude(), "" + destination.getLatitude());
            JsonNode pathNode = null;
            try {
                pathNode = objectMapper.readTree(pathInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path route = PathUtil.extractPath(pathNode);
            Rendezvous r = new Rendezvous();
            r.setName(destination.getName());
            r.setTrafficThreshold(0);
            r.setRoute(route);
            routePlan.getRendezvousList().add(r);
        }
    }

}
