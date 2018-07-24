package logisticsA.service;

import logisticsA.domain.Location;
import logisticsA.domain.Logistics;
import logisticsA.domain.RoutePlan;
import logisticsA.repos.*;
import logisticsA.domain.WagonShadow;
import logisticsA.util.CommonUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("initService")
public class InitService implements ExecutionListener, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(InitService.class);


    @Autowired
    private RestClient restClient;

    @Autowired
    private WagonShadowRepository wagonShadowRepository;

    @Autowired
    private LogisticsRepository logisticsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RoutePlanRepository routePlanRepository;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        String pid = delegateExecution.getProcessInstanceId();
        logger.info("--InitService--"+pid);
        //TODO: Save to wagon shadow
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String logisticId = pvars.get("logisticId").toString();
        String orgId =  pvars.get("orgId").toString();

        Logistics logistics = logisticsRepository.findById(logisticId);
        Location loc = locationRepository.findByName(logistics.getSupLoc());
        double sLongitude = loc.getLongitude();
        double sLatitude = loc.getLatitude();
        WagonShadow ws = new WagonShadow(logistics.getWid() , sLongitude , sLatitude,"Initiating");
        ws.setWpid(pid);
        ws.setDeltaNavDist(0);
        ws.setDeltaNavCost(0);
        ws.setLastNavsCost(0);
        wagonShadowRepository.save(ws);
        //TODO: update logistics
        logistics.setLpid(pid);


        //TODO: match with supplier
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        sendData.put("sOrgId" , logistics.getSOrgId());
        sendData.put("spid" , logistics.getSpid());
        sendData.put("wid" , logistics.getWid()); // default "null"
        sendData.put("logisticId" , logistics.getId());

        String url = commonRepository.getSlcContextPath()+"/logistics/"+orgId+"/"+pid+"/match";
        String matchSRep = restClient.matchSupplier(url , sendData);
        logger.info(matchSRep);

        //TODO : match with Vessel
        HashMap<String , Object> sendLVCData = new HashMap<String , Object>();
        sendLVCData.put("vOrgId" , logistics.getVOrgId());
        sendLVCData.put("vpid" , logistics.getVpid());
        sendLVCData.put("vid" , logistics.getVid()); // default "null"
        sendLVCData.put("logisticId" , logistics.getId());
        sendLVCData.put("wid" , logistics.getWid()); // default "null"

        String lvcUrl = commonRepository.getLvcContextPath()+"/logistics/"+orgId+"/"+pid+"/match";
        String matchVRep = restClient.matchVessel(lvcUrl , sendLVCData);
        logger.info(matchVRep);

        Map<String, Object> addiVars = new HashMap<String, Object>();
        addiVars.put("status" , "Initiating");
        runtimeService.setVariables(pid, addiVars);


        //TODO : create RoutePlan
        RoutePlan routePlan = new RoutePlan();
        String planId = "WP"+pid+CommonUtil.getGuid();
        routePlan.setId(planId);// wagon plan id;
        routePlan.setLpid(pid);
        routePlan.setMsgType("Initiating");
        HashMap<String , Object> msgBody = new HashMap<String , Object>();
        msgBody.put("eventType" , "INITIATING");
        routePlan.setMsgBody(msgBody);
        //TODO: save RoutePlan
        routePlanRepository.save(routePlan);
        runtimeService.setVariable(pid  , "planId" , planId);
    }
}
