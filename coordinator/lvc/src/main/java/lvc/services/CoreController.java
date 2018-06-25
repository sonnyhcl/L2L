package lvc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lvc.domain.*;
import lvc.repos.*;
import lvc.util.CommonUtil;
import lvc.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@SuppressWarnings("all")
public class CoreController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final double infinite = 10000000000000.0;

    private static final double k = 0.5; //紧迫性参数

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PairRepository pairRepository;

    @Autowired
    private LogisticRepository logisticRegistory;
    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private VesselRepository vesselRepository;

    @Autowired
    private StorageRepository storageRepository;

    @RequestMapping("/hello")
    String hello(){
        logger.debug("hello . lvc");
        return "hello , lvc";
    }

    @RequestMapping(value = "/vessel/{orgId}/{pid}/{msgType}" , method = RequestMethod.POST)
    public ResponseEntity<String> notifyMissing(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                                @PathVariable("msgType") String msgType){
        if(pairRepository.isPaired(orgId , pid)){
            Pair pair = pairRepository.findById(orgId , pid);
            VesselProcessInstance vpi = pair.getVpi();
            LogisticProcessInstance lpi = pair.getLpi();
            VesselPart vesselPart = vesselRepository.findByOrgId(vpi.getOrgId());
            LogisticPart logisticPart = logisticRegistory.findByLOrgId(lpi.getOrgId());

            //send request to logistic for route plan
            String rep = restClient.postStatus(logisticPart , lpi.getId() , msgType);
            logger.info(rep);
            return  new ResponseEntity<String>("{\"status\":\"success\"}" , HttpStatus.OK);
        }else{
            logger.debug("---Pair Not Found---");
            return new ResponseEntity<String>("{\"status\":\"Pair Not Found\"}" , HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/vessel/{orgId}/{pid}/delivery/status" , method = RequestMethod.GET)
    public ResponseEntity<String>  getDeliveryStatus(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid){
        if(pairRepository.isPaired(orgId , pid)){
            //TODO:get remaining destinations.
            Pair pair = pairRepository.findById(orgId , pid);
            VesselProcessInstance vpi = pair.getVpi();
            LogisticProcessInstance lpi = pair.getLpi();
            VesselPart vesselPart = vesselRepository.findByOrgId(vpi.getOrgId());
            LogisticPart logisticPart = logisticRegistory.findByLOrgId(lpi.getOrgId());

            //TODO:get remaining destinations.
            List<Destination> vDestinations = restClient.getRemaingDestinations(vesselPart , vpi.getId());
            //TODO:get  logistic
            Logistic logistic = restClient.getLogistic(logisticPart , lpi.getId());
            List<String> rends = logistic.getDestinations();

            List<String> res = new ArrayList<String>();

            for(Destination d : vDestinations){
                if(rends.contains(d.getName())){
                    res.add(d.getName());
                }
            }
            if(res.size() > 0){
                return  new ResponseEntity<String>("NOT_MISSING" , HttpStatus.OK);
            }else{
                return  new ResponseEntity<String>("MISSING" , HttpStatus.OK);
            }
        }else{
            logger.debug("---Pair Not Found---");
            return  new ResponseEntity<String>("NOT_PAIRED" , HttpStatus.OK);
        }

    }


    @RequestMapping(value = "/logistic/{orgId}/{pid}/{msgType}" , method = RequestMethod.POST)
    public ResponseEntity<String> arrivalNotify(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                                @PathVariable("msgType") String msgType){
        Pair pair = pairRepository.findById(orgId , pid);
        VesselProcessInstance vpi = pair.getVpi();
        LogisticProcessInstance lpi = pair.getLpi();
        VesselPart vesselPart = vesselRepository.findByOrgId(vpi.getOrgId());
        LogisticPart logisticPart = logisticRegistory.findByLOrgId(lpi.getOrgId());

        String rep = restClient.postStatus(vesselPart , vpi.getId() , msgType);
        logger.info(rep);
        return  new ResponseEntity<String>("{\"status\":\"success\"}" , HttpStatus.OK);

    }


    @RequestMapping(value = "/logistic/{orgId}/{pid}/route/decide", method = RequestMethod.POST)
    public ResponseEntity<Rendezvous> routteDecideRendezvous(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                                                @RequestBody RoutePlan routePlan) throws JsonProcessingException {

        logger.debug("Received message : route decide  from logistic part : "+orgId+"--PID: "+pid);
        //TODO:Supplier shakes hands with logistic.

        Pair pair = pairRepository.findById(orgId , pid);
        VesselProcessInstance vpi = pair.getVpi();
        LogisticProcessInstance lpi = pair.getLpi();
        List<Rendezvous> rendezvousList = routePlan.getRendezvousList();

        VesselPart vesselPart = vesselRepository.findByOrgId(vpi.getOrgId());
        //TODO:get remaining destinations.
        List<Destination> vDestinations = restClient.getRemaingDestinations(vesselPart , vpi.getId());
        //TODO:get current time(ms)
        long currentMs = restClient.getCurrentMs(vesselPart , vpi.getId());

        LogisticPart logisticPart = logisticRegistory.findByLOrgId(lpi.getOrgId());
        //TODO:get frieght rates
        List<Freight> wFreightRates = restClient.getFreights(logisticPart);
        freightRepository.setFreights(wFreightRates);
        //TODO:get  logistic
        Logistic logistic = restClient.getLogistic(logisticPart , lpi.getId());
        double spWeight = logistic.getSpWight();

        rendezvousList.size();
        List<Bill> bills = new ArrayList<Bill>();
        List<Rendezvous> resultRends = new ArrayList<Rendezvous>();
        List<String> candidateRends = new ArrayList<String>();
        logger.debug("---Select solution meeting with constrains.---");
        //TODO: check remaining destinations
        if(vDestinations.size() == 0){
            logger.debug("---all candidate ports have passed!---");
            Rendezvous tRen = new Rendezvous();
            missing(routePlan);
            return new ResponseEntity<Rendezvous>(routePlan.getRendezvous() , HttpStatus.OK);
        }
        //TODO: check candidate rendezous
        if(rendezvousList.size() == 0){
            logger.debug("---No Candidate Rendezvous---");
            missing(routePlan);
            return new ResponseEntity<Rendezvous>(routePlan.getRendezvous() , HttpStatus.OK);
        }
        for(int i = 0 ; i < rendezvousList.size() ; i++){ //Scan optional rendezvousList to ensure sequence of arrival at rendezvous is consistent with the index in rendezvousList.
            //TODO:screening the ports that have passed
            Rendezvous tRend = rendezvousList.get(i);
            Destination vDest = CommonUtil.findByName(tRend.getName() , vDestinations);
            if(vDest != null){
                Date wEstiArrivalDate = DateUtil.transForDate(tRend.getRoute().getDuration()*1000+currentMs);
                String wEstiArrivalTime= DateUtil.date2str(wEstiArrivalDate);
                String vEstiArrivalTime = vDest.getEstiArrivalTime();
                String vEstiDespatureTime = vDest.getEstiDepartureTime();
                logger.debug("Vessel: vEstiArrivalTime = "+vEstiArrivalTime+" vEstiDespatureTime = "+vEstiDespatureTime);
                logger.debug(("Wagon wEstiArrivalTime = "+wEstiArrivalTime));
                double storageRate = storageRepository.findByName(tRend.getName());
                double freightRate = freightRepository.findByName(tRend.getName());
                long wEstiDistance = tRend.getRoute().getDistance();
                if(DateUtil.TimeMinus(vEstiDespatureTime , wEstiArrivalTime) > 0){ // require arrival time of wagon  before despature time of vessel.
                    double storageCost = Math.max(DateUtil.TimeMinus(vEstiArrivalTime, wEstiArrivalTime), 0)*storageRate*spWeight/(1000 * 60 * 60);
                    double freightCost = wEstiDistance*freightRate*spWeight;
                    resultRends.add(tRend);
                    bills.add(new Bill(tRend.getName() , freightCost ,storageCost));
                    candidateRends.add(tRend.getName());
                    logger.debug("Desination : "+tRend.getName()+" Frieght Cost : "+freightCost+" Storage Cost : "+storageCost);
                }
            }
        }

        //TODO: find best rendezvous
        Rendezvous bestRend = null;
        double minCost = infinite;
        int len = resultRends.size();
        logger.debug("---Select optimized solution based on the time urgency.---");
        if(len == 0){
        }else {
            for (int i = 0; i < len; i++) {
                Rendezvous tRend = resultRends.get(i);
                double sumCost = CommonUtil.sumCost(tRend.getName(), bills);
                double optimizedCost = (1 - Math.pow(k, i + 1)) * sumCost;
                logger.debug("Desination : " + tRend.getName() + " Optimized Cost : " + optimizedCost + " Original Cost : " + sumCost);
                if (optimizedCost < minCost) {
                    minCost = optimizedCost;
                    bestRend = tRend;
                }
            }
            if (bestRend != null) {
                logger.info("--The best redezvous with the lowest cost is planned out!--" + bestRend.getName());
                //TODO: update route plan.
                routePlan.setRendezvous(bestRend);
                routePlan.setRendezvousList(resultRends);
                //TODO: update logistic
                logger.info("---update logistic---");
                logistic.setDestinations(candidateRends);
                logistic.setRendezous(bestRend.getName());
                logistic.setTimeStamp(DateUtil.date2str(DateUtil.transForDate(currentMs)));
                restClient.putLogistic(logisticPart, lpi.getId(), logistic);
            } else {
                logger.debug("---Best Rendezvous Not Found---");
                missing(routePlan);
            }
        }




        logger.info("Deciding is completed");
        return new ResponseEntity<Rendezvous>(routePlan.getRendezvous() , HttpStatus.OK);
    }

    @RequestMapping(value = "/logistic/{orgId}/{pid}/match", method = RequestMethod.POST)
    public String match(@PathVariable("orgId") String orgId , @PathVariable("pid") String pid ,
                                   @RequestBody HashMap<String, Object> payload) throws JsonProcessingException {
        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        logger.debug("Received message : match from logistic-app : "+orgId+"--PID: "+pid);
        //TODO:Ship shakes hands with the affiliated shipping management company.
        String vOrgId = payload.get("vOrgId").toString();
        String vpid = payload.get("vpid").toString();
        String vid = payload.get("vid").toString();
        String wid = payload.get("wid").toString();
        String logisticId = payload.get("logisticId").toString();
        VesselPart vesselPart = vesselRepository.findByOrgId(vOrgId);
        LogisticProcessInstance lpi = new LogisticProcessInstance(pid , orgId , logisticId ,wid);
        VesselProcessInstance vpi = new VesselProcessInstance(vpid , vOrgId , vid);
        pairRepository.createPair(vpi , lpi);
        logger.info(pairRepository.getPairs().toString());
        return "L-V match successfully";
    }

    private void missing(RoutePlan routePlan){
        Rendezvous tRen = new Rendezvous();
        tRen.setName("MISSING");
        routePlan.setRendezvous(tRen);
        routePlan.setRendezvousList(null);
    }

    private void notmatched(RoutePlan routePlan){
        Rendezvous tRen = new Rendezvous();
        tRen.setName("NOT_MATCHED");
        routePlan.setRendezvous(tRen);
        routePlan.setRendezvousList(null);
    }


}
