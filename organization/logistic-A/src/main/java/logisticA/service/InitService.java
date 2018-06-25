package logisticA.service;

import logisticA.domain.Location;
import logisticA.domain.Logistic;
import logisticA.domain.WagonShadow;
import logisticA.repos.CommonRepository;
import logisticA.repos.LocationRepository;
import logisticA.repos.LogisticRepository;
import logisticA.repos.WagonShadowRepository;
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
    private LogisticRepository logisticRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        String pid = delegateExecution.getProcessInstanceId();
        logger.info("--InitService--"+pid);
        //TODO: Save to wagon shadow
        HashMap<String, Object> pvars = (HashMap<String, Object>) runtimeService.getVariables(pid);
        String logisticId = pvars.get("logisticId").toString();
        String orgId =  pvars.get("orgId").toString();

        Logistic logistic = logisticRepository.findById(logisticId);
        Location loc = locationRepository.findByName(logistic.getSupLoc());
        double sLongitude = loc.getLongitude();
        double sLatitude = loc.getLatitude();
        WagonShadow ws = new WagonShadow(logistic.getWid() , sLongitude , sLatitude,"Initiating");
        ws.setWpid(pid);
        wagonShadowRepository.save(ws);
        //TODO: update logistic
        logistic.setLpid(pid);


        //TODO: match with supplier
        HashMap<String , Object> sendData = new HashMap<String , Object>();
        sendData.put("sOrgId" , logistic.getSOrgId());
        sendData.put("spid" , logistic.getSpid());
        sendData.put("wid" , logistic.getWid()); // default "null"
        sendData.put("logisticId" , logistic.getId());

        String url = commonRepository.getSlcContextPath()+"/logistic/"+orgId+"/"+pid+"/match";
        String matchSRep = restClient.matchSupplier(url , sendData);
        logger.info(matchSRep);

        //TODO : match with Vessel
        HashMap<String , Object> sendLVCData = new HashMap<String , Object>();
        sendLVCData.put("vOrgId" , logistic.getVOrgId());
        sendLVCData.put("vpid" , logistic.getVpid());
        sendLVCData.put("vid" , logistic.getVid()); // default "null"
        sendLVCData.put("logisticId" , logistic.getId());
        sendLVCData.put("wid" , logistic.getWid()); // default "null"

        String lvcUrl = commonRepository.getLvcContextPath()+"/logistic/"+orgId+"/"+pid+"/match";
        String matchVRep = restClient.matchVessel(lvcUrl , sendLVCData);
        logger.info(matchVRep);

        Map<String, Object> addiVars = new HashMap<String, Object>();
        addiVars.put("status" , "Initiating");
        runtimeService.setVariables(pid, addiVars);
    }
}
