package slc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slc.domain.*;
import slc.repos.LogisticsPartRepository;
import slc.repos.PairRepository;
import slc.repos.SupplierPartRepository;
import slc.util.CommonUtil;

@Service
@SuppressWarnings("all")
public class DecisionMaking {
    private static final Logger logger = LoggerFactory.getLogger(DecisionMaking.class);

    @Autowired
    private RestClient restClient;

    @Autowired
    private StompClient stompClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticsPartRepository logisticsPartRepository;

    @Autowired
    private SupplierPartRepository supplierPartRepository;

    @Autowired
    private AsyncTaskService asyncTaskService;

    public  Logistics arrangeLogistics(String orgId, String pid, Logistics logistics) {
        //TODO:Supplier shakes hands with logistics.
        //TODO: allocate wid , logisticId
        String wid = "W" + CommonUtil.getGuid();
        String logisticId = pid + CommonUtil.getGuid();

        SupplierPart sp = supplierPartRepository.findByOrgId(orgId);
        SupplierProcessInstance spi = new SupplierProcessInstance(pid, orgId, null, logisticId);
        spi.setLogisticId(logistics.getId());
        if (pairRepository.isRegistried(orgId, pid) == false) {
            Pair pair = new Pair(spi, null);
            pairRepository.register(pair);
        }

        //TODO: Starting Logistics based on message.
        String lOrgId = logistics.getLOrgId();
        LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lOrgId);
        logistics.setWid(wid);
        logistics.setId(logisticId);
        asyncTaskService.startLogistics(logisticsPart, logistics);
        return  logistics;
    }
}
