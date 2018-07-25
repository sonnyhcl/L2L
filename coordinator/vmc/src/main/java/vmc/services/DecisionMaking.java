package vmc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vmc.domain.*;
import vmc.repos.ManagerPartRepository;
import vmc.repos.PairRepository;
import vmc.repos.VesselPartRepository;
import vmc.util.CommonUtil;

@Service
@SuppressWarnings("all")
public class DecisionMaking {
    private static final Logger logger = LoggerFactory.getLogger(DecisionMaking.class);

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private ManagerPartRepository managerPartRepository;

    @Autowired
    private VesselPartRepository vesselPartRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private StompClient stompClient;


    public  Application  apply(String orgId, String pid , Application application){
        //TODO: genarete applyId
        String applyId = pid+ CommonUtil.getGuid();
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vid = application.getVid();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(orgId);
        VesselProcessInstance vpi = new VesselProcessInstance(pid , orgId , vid);
        if(pairRepository.isRegistried(orgId , pid) == false){
            Pair pair = new Pair(vpi , null);
            pairRepository.register(pair);
        }

        //TODO:select manager for application

        String mOrgId = "MA1001";
        ManagerPart mpart= managerPartRepository.findByOrgId(mOrgId);
        //TODO:send MsgStartManager to manager
        application.setId(applyId);
        application.setMOrgId(mOrgId);
        String url = mpart.getUrl()+"/api/"+orgId+"/process-instances/MsgStartManager";
        stompClient.showManager(mpart);
        asyncTaskService.startManager(url , application , vesselPart);
        return application;
    }


}
