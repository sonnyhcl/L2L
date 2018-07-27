package lvc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lvc.domain.*;
import lvc.repos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@SuppressWarnings("all")
public class RelayStation {

    private static final Logger logger = LoggerFactory.getLogger(RelayStation.class);
    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticsPartRepository logisticsPartRepository;
    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private VesselPartRepository vesselPartRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private DecisionMaking decisionMaking;

    @RequestMapping("/hello")
    String hello() {
        logger.debug("hello . lvc");
        return "hello , lvc";
    }

    @RequestMapping(value = "/vessel/{orgId}/{pid}/{msgType}", method = RequestMethod.POST)
    public ResponseEntity<String> notifyMissing(@PathVariable("orgId") String orgId, @PathVariable("pid") String pid,
                                                @PathVariable("msgType") String msgType, @RequestBody HashMap<String, Object> mp) {
        if (pairRepository.isPaired(orgId, pid)) {
            Pair pair = pairRepository.findById(orgId, pid);
            VesselProcessInstance vpi = pair.getVpi();
            LogisticsProcessInstance lpi = pair.getLpi();
            VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
            LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lpi.getOrgId());

            String eventType = mp.get("eventType").toString();
            logger.debug("event from vessel  : " + eventType);
            switch (eventType) {
                case "DELAY":
                    break;
                case "MISSING":
                    break;
                case "MEETING":
                    break;
                default:
                    break;
            }

            //send request to logistics for route plan
            String rep = restClient.postStatus(logisticsPart, lpi.getId(), msgType, mp);
            logger.info(rep);
            return new ResponseEntity<String>("{\"status\":\"success\"}", HttpStatus.OK);
        } else {
            logger.debug("---Pair Not Found---");
            return new ResponseEntity<String>("{\"status\":\"Pair Not Found\"}", HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/vessel/{orgId}/{pid}/delivery/status", method = RequestMethod.GET)
    public ResponseEntity<String> getDeliveryStatus(@PathVariable("orgId") String orgId, @PathVariable("pid") String pid) {
        String delivaryStatus = null;
        logger.debug("--check delivery status--");

        if (pairRepository.isPaired(orgId, pid)) {
            //TODO:get remaining destinations.
            Pair pair = pairRepository.findById(orgId, pid);
            VesselProcessInstance vpi = pair.getVpi();
            LogisticsProcessInstance lpi = pair.getLpi();
            VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
            LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lpi.getOrgId());

            //TODO:get remaining destinations.
            List<Destination> vDestinations = restClient.getRemaingDestinations(vesselPart, vpi.getId());
            //TODO:get  logistics
            Logistics logistics = restClient.getLogistic(logisticsPart, lpi.getId());
            String policy = logistics.getCategory();
            switch (policy) {
                case "fixed-destination":
                    delivaryStatus = decisionMaking.checkDeliveryStatusOnFD(logisticsPart, lpi.getId(), vDestinations, logistics);
                    break;
                case "variable-destination":
                    delivaryStatus = decisionMaking.checkDeliveryStatusOnVD(logisticsPart, lpi.getId(), vDestinations, logistics);
                    break;
                default:
                    break;
            }
        } else {
            logger.debug("---Pair Not Found---");
            delivaryStatus = "NOT_PAIRED";
        }
        return new ResponseEntity<String>(delivaryStatus, HttpStatus.OK);
    }


    @RequestMapping(value = "/logistics/{orgId}/{pid}/route/decide", method = RequestMethod.POST)
    public ResponseEntity<RoutePlan> routeDecideRendezvous(@PathVariable("orgId") String orgId, @PathVariable("pid") String pid,
                                                            @RequestBody RoutePlan routePlan) throws JsonProcessingException {

        logger.debug("Received message : route decide  from logistics part : " + orgId + "--PID: " + pid);
        Pair pair = pairRepository.findById(orgId, pid);
        VesselProcessInstance vpi = pair.getVpi();
        LogisticsProcessInstance lpi = pair.getLpi();
        List<Rendezvous> rendezvousList = routePlan.getRendezvousList();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
        LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lpi.getOrgId());
        //TODO:get  logistics
        Logistics logistics = restClient.getLogistic(logisticsPart, lpi.getId());
        String policy = logistics.getCategory();
        switch (policy) {
            case "fixed-destination":
                routePlan = decisionMaking.decideRouteByFixedDestination(orgId, pid, routePlan);
                break;
            case "variable-destination":
                routePlan = decisionMaking.decideRouteByVariableDestination(orgId, pid, routePlan);
                break;
            default:

                break;
        }
        logger.info("Deciding is completed");
        return new ResponseEntity<RoutePlan>(routePlan, HttpStatus.OK);
    }

    @RequestMapping(value = "/logistics/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String match(@PathVariable("orgId") String orgId, @PathVariable("pid") String pid,
                        @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from logistic-app : " + orgId + "--PID: " + pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vOrgId = payload.get("vOrgId").toString();
        String vpid = payload.get("vpid").toString();
        String vid = payload.get("vid").toString();
        String wid = payload.get("wid").toString();
        String logisticId = payload.get("logisticId").toString();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(vOrgId);
        LogisticsProcessInstance lpi = new LogisticsProcessInstance(pid, orgId, logisticId, wid);
        VesselProcessInstance vpi = new VesselProcessInstance(vpid, vOrgId, vid);
        pairRepository.createPair(vpi, lpi);
        logger.info(pairRepository.getPairs().toString());
        return "L-V match successfully";
    }


}
