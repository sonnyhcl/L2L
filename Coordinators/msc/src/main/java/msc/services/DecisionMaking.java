package msc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import msc.Repos.CraneRepository;
import msc.Repos.ManagerPartRepository;
import msc.Repos.PairRepository;
import msc.Repos.SupplierPartRepository;
import msc.domain.*;
import msc.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private CraneRepository craneRepository;

    @Autowired
    private SupplierPartRepository supplierPartRepository;

    @Autowired
    private ManagerPartRepository managerPartRepository;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private  AsyncTaskService asyncTaskService;

    public Order order(String orgId, String pid, Order order) {
        //TODO: generate ordId
        String orderId = pid + CommonUtil.getGuid();
        //TODO: ship shakes hands with the affiliated shipping  company.
        ManagerPart managerPart = managerPartRepository.findByOrgId(orgId);
        ManagerProcessInstance mpi = new ManagerProcessInstance(pid, orgId);
        mpi.setOrderId(orderId);
        if (pairRepository.isRegistried(orgId, pid) == false) {
            Pair pair = new Pair(mpi, null);
            pairRepository.register(pair);
        }

        //TODO: according to the spare parts information, choose the best supplier according to the corresponding policy
        String spName = order.getSpName();
        String sOrgId = null;
        sOrgId = supplierPartRepository.getSupplierParts().get(0).getOrgId(); // default  for simplicity, select the first supplier.
        order.setSOrgId(sOrgId);  //select a supplier for order.
        SupplierPart supplierPart = supplierPartRepository.findByOrgId(sOrgId);
        stompClient.showSupplier(supplierPart);

        //TODO: according to crane information , screen effective ports.
        List<String> validDests = new ArrayList<String>();
        List<String> invalidDests = new ArrayList<String>();
        List<String> destinations = order.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            String d = destinations.get(i);
            double wlim = craneRepository.queryWeightLimit(d);
            if (order.getSpWight() <= wlim) {
                validDests.add(d);
            } else {
                invalidDests.add(d);
            }
        }
        order.setDestinations(validDests);
        stompClient.showInvlidRendezvousPorts(invalidDests);
        //TODO: starting Supplier based on message.
        order.setId(orderId);
        asyncTaskService.startSupplier(supplierPart , order);
        return order;
    }
}
