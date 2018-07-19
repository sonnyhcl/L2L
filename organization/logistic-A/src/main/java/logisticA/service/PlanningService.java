package logisticA.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import logisticA.repos.RoutePlanRepository;
import logisticA.domain.*;
import logisticA.repos.*;
import logisticA.util.PathUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Service("planningService")
public class PlanningService implements JavaDelegate ,Serializable{

    private static final Logger logger = LoggerFactory.getLogger(PlanningService.class);

    private static final String topic = "activiti/wagon/navigate";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RestClient restClient;

    @Autowired
    private LogisticRepository logisticRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    public SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RoutePlanRepository routePlanRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO: generate ordId for applying order
        String pid = delegateExecution.getProcessInstanceId();
        logger.info("************************PlanningService************************"+pid);

        //TODO:获取流程数据
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String logisticId = pvars.get("logisticId").toString();
        String orgId =  pvars.get("orgId").toString();
        String wid = pvars.get("wid").toString();
        String planId = pvars.get("planId").toString();
        String status = pvars.get("status").toString();

        //TODO: 如果不是第一次规划，先停止仿真器　，然后规划
        if(!status.equals("Initiating")){
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("msgType" , "PAUSE");
            payload.put("from" , wid);
            simpMessagingTemplate.convertAndSendToUser( "admin","/topic/wagon/pause" , payload);
        }
        //TODO: 获取物流信息
        Logistic logistic = logisticRepository.findById(logisticId);
        WagonShadow wagonShadow = wagonShadowRepository.findById(wid);
        List<String> destinations = logistic.getDestinations();
        int size = destinations.size();
        //TODO: 获取当前规划对象
        RoutePlan routePlan = routePlanRepository.findById(planId);
        if(routePlan != null){
            logger.debug("size : "+routePlanRepository.getRoutePlans().size());
            logger.debug("route plan : "+ size);
        }
        for(int i = 0 ; i < size ; i++){
            logger.debug("route plan... "+ i);
            Location destination = locationRepository.findByName(destinations.get(i));
            //double to string ""+x
            String pathInfo = mapRepository.PlanPath(""+wagonShadow.getLongitude() , ""+wagonShadow.getLatitude()
                    , ""+destination.getLongitude() , ""+destination.getLatitude());
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
//            logger.debug("route plan done "+ i);
        }

        //TODO : decide rendezvous
        String url = commonRepository.getLvcContextPath()+"/logistic/"+orgId+"/"+pid+"/route/decide";
        RoutePlan decidedRoutePlan = restClient.decide(url , routePlan);
        routePlan.setRendezvousList(decidedRoutePlan.getRendezvousList());
        routePlan.setRendezvous(decidedRoutePlan.getRendezvous());
        String destName = routePlan.getRendezvous().getName();
        logger.debug("destination : "+destName);

        switch (destName){
            case "MISSING" :
                logger.debug("Missing."); //协作建立前，已出现Ｍissing , 协作建立后如果出现Missing, 肯定是vessel最先发现
                ObjectNode payload = objectMapper.createObjectNode();
                payload = objectMapper.createObjectNode();
                payload.put("from" , pid);
                payload.put("reason" , "车船错过交货机会");
                payload.put("C0" , routePlanRepository.getC2(pid));
                payload.put("C1" , -1);//这里使用第二次，不改变目的港口，代表不对变化做出决策，即因变化二需要的新成本
                payload.put("C2" , -1);
                payload.put("isFirst" , true);
                simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route/missing" , payload);
                wagonShadow.setStatus("MISSING");
                break;
            case "NOT_MATCHED" :
                logger.debug("Not matched."); // 基本上不会发生
                break;
            case "FAIL" :
            default :
                //TODO: send route info to navigator for displaying track and simulating running , navigator represents the device side.
                payload = objectMapper.createObjectNode();
                payload.put("from" , pid);
                payload.put("reason" , routePlanRepository.getReason(pid));
                payload.put("C0" , routePlanRepository.getC0(pid));
                payload.put("C1" , routePlanRepository.getC1(pid));//这里使用第二次，不改变目的港口，代表不对变化做出决策，即因变化二需要的新成本
                payload.put("C2" , routePlanRepository.getC2(pid));
                if(routePlanRepository.getEventType(pid).equals("INITIATING")){
                    payload.put("isFirst" , true);
                }else{
                    payload.put("isFirst" , false);
                }
                if(destName.equals("FAIL")){
                    payload.put("msgType" , "FAIL");
                    simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route/fail" , payload);
                }else{
                    wagonShadow.setLastNavsCost(wagonShadow.getLastNavsCost()+wagonShadow.getDeltaNavCost());
//                    payload.put("passedCost" , wagonShadow.getLastNavsCost());
                    payload.putPOJO("msgBody" , routePlan.getRendezvous());
                    payload.put("msgType" , "SUCCESS");
                    simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route/success" , payload);
                    wagonShadow.setRendezvous(routePlan.getRendezvous());//规划成功才设置会合港口
                }
                simpMessagingTemplate.convertAndSendToUser( "admin","/topic/route" , payload);
                runtimeService.setVariable(pid , "status" , "Running");

                //TODO: update wagon shadow

                wagonShadow.setStatus("Running");
                break;
        }

    }
}
