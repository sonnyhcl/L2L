package lvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lvc.domain.*;
import lvc.repos.*;
import lvc.util.CommonUtil;
import lvc.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@SuppressWarnings("all")
public class DecisionMaking {
    private static final Logger logger = LoggerFactory.getLogger(DecisionMaking.class);
    private static final double infinite = 10000000000000.0;

    private static final double k = 0.5; //紧迫性参数

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

    public String  checkDeliveryStatusOnVD(LogisticsPart logisticsPart ,  String lpid ,  List<Destination> vDestinations , Logistics logistics){
        List<String> res = new ArrayList<String>();
        List<String> rends = logistics.getDestinations();
        for (Destination d : vDestinations) {
            if (rends.contains(d.getName())) {
                res.add(d.getName());
            }
        }
        if (res.size() > 0) {
            return "NOT_MISSING";
        } else {
            //TODO: notify logistics of "Missing"
            HashMap<String, Object> msgBody = new HashMap<String, Object>();
            msgBody.put("eventType", "MISSING");
            String rep = restClient.postStatus(logisticsPart, lpid , "Missing", msgBody);
            return  "MISSING";
        }

    }
    public String  checkDeliveryStatusOnFD(LogisticsPart logisticsPart ,  String lpid ,  List<Destination> vDestinations , Logistics logistics){
        logger.debug("--checkDeliveryStatusOnFD--");
               //check whether the wagon arrived at the port
        Pair pair = pairRepository.findById(logisticsPart.getOrgId() , lpid);
        VesselProcessInstance vpi = pair.getVpi();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
        Application application = restClient.getApplication(vesselPart , vpi.getId());
        String applyId = application.getId();
       String rendPort =  application.getRendezvous();
       logger.debug(application.toString());
       String res = null;
        if(!applyId.equals("NONE")){
             if(rendPort.equals("NONE")){

                 res = checkDeliveryStatusOnVD(logisticsPart , lpid , vDestinations , logistics);
                 logger.debug("None "+res);
                 return res;
             }else{
                    Destination destination = restClient.getCurrentPort(vesselPart , vpi.getId());
                    String wagonStatus = restClient.getWagonStatus(logisticsPart , lpid);
                    boolean  wagonArrival = wagonStatus.equals("Arrival");
                    boolean vesselArrival =  destination.getName().equals(rendPort);
                    logger.debug("wagon status : "+wagonStatus+ "--"+destination.getName()+" rend : "+rendPort);
                    if(wagonArrival == true && vesselArrival == true ){
                        res = "MEETING";
                    }else if(wagonArrival == false &&  vesselArrival == true){
                        res =  "MISSING";
                    }else{
                        res = "IGNORE";
                    }
             }
        }else{
            res = "IGNORE";
        }
        logger.debug(res);
        return res;
    }

    public RoutePlan  decideRouteByVariableDestination(String orgId , String pid ,  RoutePlan routePlan ){
        Pair pair = pairRepository.findById(orgId, pid);
        VesselProcessInstance vpi = pair.getVpi();
        LogisticsProcessInstance lpi = pair.getLpi();
        List<Rendezvous> rendezvousList = routePlan.getRendezvousList();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
        //TODO:get remaining destinations.
        List<Destination> vDestinations = restClient.getRemaingDestinations(vesselPart, vpi.getId());
        //TODO:get current time(ms)
        long currentMs = restClient.getCurrentMs(vesselPart, vpi.getId());
        LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lpi.getOrgId());
        //TODO:get frieght rates
        List<Freight> wFreightRates = restClient.getFreights(logisticsPart);
        freightRepository.setFreights(wFreightRates);
        //TODO:get  logistics
        Logistics logistics = restClient.getLogistic(logisticsPart, lpi.getId());

        double spWeight = logistics.getSpWight();
        rendezvousList.size();
        List<Bill> bills = new ArrayList<Bill>();
        List<Rendezvous> resultRends = new ArrayList<Rendezvous>();
        List<String> candidateRends = new ArrayList<String>();
        logger.debug("---Select solution meeting with constrains.---");
        //TODO: check remaining destinations
        if (vDestinations.size() == 0) {
            logger.debug("---all candidate ports have passed!---");
            Rendezvous tRen = new Rendezvous();
            missing(routePlan);
            return routePlan;
        }
        //TODO: check candidate rendezous
        if (rendezvousList.size() == 0) {
            logger.debug("---No Candidate Rendezvous---");
            missing(routePlan);
            return routePlan;
        }
        for (int i = 0; i < rendezvousList.size(); i++) { //Scan optional rendezvousList to ensure sequence of arrival at rendezvous is consistent with the index in rendezvousList.
            //TODO:screening the ports that have passed
            Rendezvous tRend = rendezvousList.get(i);
            Destination vDest = CommonUtil.findByName(tRend.getName(), vDestinations);
            if (vDest != null) {
                Date wEstiArrivalDate = DateUtil.transForDate(tRend.getRoute().getDuration() * 1000 + currentMs);
                String wEstiArrivalTime = DateUtil.date2str(wEstiArrivalDate);
                String vEstiArrivalTime = vDest.getEstiArrivalTime();
                String vEstiDespatureTime = vDest.getEstiDepartureTime();

                logger.debug("Vessel: vEstiArrivalTime = " + vEstiArrivalTime + " vEstiDespatureTime = " + vEstiDespatureTime);
                logger.debug(("Wagon: wEstiArrivalTime = " + wEstiArrivalTime));
                double storageRate = storageRepository.findByName(tRend.getName());
                double freightRate = freightRepository.findByName(tRend.getName());
                long wEstiDistance = tRend.getRoute().getDistance();
                if (DateUtil.TimeMinus(vEstiDespatureTime, wEstiArrivalTime) > 0) { // require arrival time of wagon  before despature time of vessel.
                    double storageCost = Math.max(DateUtil.TimeMinus(vEstiArrivalTime, wEstiArrivalTime), 0) * storageRate * spWeight / (1000 * 60 * 60);
                    double freightCost = wEstiDistance * freightRate * spWeight;
                    tRend.setTrafficThreshold(DateUtil.TimeMinus(vEstiDespatureTime, wEstiArrivalTime) / 1000);
                    //TODO: 加上已经产生的成本
                    double generatedCost = restClient.getCurrentCost(logisticsPart, lpi.getId());
                    tRend.setSumCost(freightCost + storageCost + generatedCost);
                    resultRends.add(tRend);
                    bills.add(new Bill(tRend.getName(), freightCost, storageCost));
                    logger.debug("Desination : " + tRend.getName() + " Frieght Cost : " + freightCost + " Storage Cost : " + storageCost + "currentCost" + generatedCost
                            + " sum : " + (freightCost + storageCost + generatedCost));
                }
                //TODO: 将还有机会，但可能不满足到达时间约束的港口加入候选港口
                candidateRends.add(tRend.getName());
            }

        }

        logistics.setDestinations(candidateRends);
        //TODO: find best rendezvous
        Rendezvous bestRend = null;
        double minCost = infinite;
        int len = resultRends.size();
        logger.debug("---Select optimized solution based on the time urgency.---");
        if (len == 0) {
            logger.debug("No ports meeting the constrain.");
            logger.debug("Decision fail.");
            fail(routePlan);
        } else {
            for (int i = 0; i < len; i++) {
                Rendezvous tRend = resultRends.get(i);
                double sumCost = tRend.getSumCost();
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
                //TODO: update logistics
                logger.info("---update logistics---");
                logistics.setRendezous(bestRend.getName());
                logistics.setTimeStamp(DateUtil.date2str(DateUtil.transForDate(currentMs)));
                restClient.putLogistic(logisticsPart, lpi.getId(), logistics);
                logger.debug("send rendezvous port to vessel : " + bestRend.getName());
                restClient.putRendezvous(vesselPart, vpi.getId(), bestRend.getName());
            } else {
                logger.debug("---Best Rendezvous Not Found---");
                missing(routePlan);
            }
        }
        return routePlan;
    }

    public RoutePlan decideRouteByFixedDestination(String orgId , String pid , RoutePlan routePlan){
        Pair pair = pairRepository.findById(orgId, pid);
        VesselProcessInstance vpi = pair.getVpi();
        LogisticsProcessInstance lpi = pair.getLpi();
        List<Rendezvous> rendezvousList = routePlan.getRendezvousList();
        VesselPart vesselPart = vesselPartRepository.findByOrgId(vpi.getOrgId());
        //TODO:get remaining destinations.
        List<Destination> vDestinations = restClient.getRemaingDestinations(vesselPart, vpi.getId());
        //TODO:get current time(ms)
        long currentMs = restClient.getCurrentMs(vesselPart, vpi.getId());
        LogisticsPart logisticsPart = logisticsPartRepository.findByLOrgId(lpi.getOrgId());
        //TODO:get frieght rates
        List<Freight> wFreightRates = restClient.getFreights(logisticsPart);
        freightRepository.setFreights(wFreightRates);
        //TODO:get  logistics
        Logistics logistics = restClient.getLogistic(logisticsPart, lpi.getId());

        double spWeight = logistics.getSpWight();
        rendezvousList.size();
        List<Bill> bills = new ArrayList<Bill>();
        List<Rendezvous> resultRends = new ArrayList<Rendezvous>();
        List<String> candidateRends = new ArrayList<String>();
        logger.debug("---Select solution meeting with constrains.---");
        //TODO: check remaining destinations
        if (vDestinations.size() == 0) {
            logger.debug("---all candidate ports have passed!---");
            Rendezvous tRen = new Rendezvous();
            missing(routePlan);
            return routePlan;
        }
        //TODO: check candidate rendezous
        if (rendezvousList.size() == 0) {
            logger.debug("---No Candidate Rendezvous---");
            missing(routePlan);
            return routePlan;
        }
        for (int i = 0; i < rendezvousList.size(); i++) { //Scan optional rendezvousList to ensure sequence of arrival at rendezvous is consistent with the index in rendezvousList.
            //TODO:screening the ports that have passed
            Rendezvous tRend = rendezvousList.get(i);
            Destination vDest = CommonUtil.findByName(tRend.getName(), vDestinations);
            if (vDest != null) {
                Date wEstiArrivalDate = DateUtil.transForDate(tRend.getRoute().getDuration() * 1000 + currentMs);
                String wEstiArrivalTime = DateUtil.date2str(wEstiArrivalDate);
                String vEstiArrivalTime = vDest.getEstiArrivalTime();
                String vEstiDespatureTime = vDest.getEstiDepartureTime();

                logger.debug("Vessel: vEstiArrivalTime = " + vEstiArrivalTime + " vEstiDespatureTime = " + vEstiDespatureTime);
                logger.debug(("Wagon: wEstiArrivalTime = " + wEstiArrivalTime));
                double storageRate = storageRepository.findByName(tRend.getName());
                double freightRate = freightRepository.findByName(tRend.getName());
                long wEstiDistance = tRend.getRoute().getDistance();
                if (DateUtil.TimeMinus(vEstiDespatureTime, wEstiArrivalTime) > 0) { // require arrival time of wagon  before despature time of vessel.
                    double storageCost = Math.max(DateUtil.TimeMinus(vEstiArrivalTime, wEstiArrivalTime), 0) * storageRate * spWeight / (1000 * 60 * 60);
                    double freightCost = wEstiDistance * freightRate * spWeight * 0.7; // freight   charge is lower.
                    tRend.setTrafficThreshold(DateUtil.TimeMinus(vEstiDespatureTime, wEstiArrivalTime) / 1000);
                    //TODO: 加上已经产生的成本
                    double generatedCost = restClient.getCurrentCost(logisticsPart, lpi.getId());
                    tRend.setSumCost(freightCost + storageCost + generatedCost);
                    resultRends.add(tRend);
                    bills.add(new Bill(tRend.getName(), freightCost, storageCost));
                    logger.debug("Desination : " + tRend.getName() + " Frieght Cost : " + freightCost + " Storage Cost : " + storageCost + "currentCost" + generatedCost
                            + " sum : " + (freightCost + storageCost + generatedCost));
                }
                //TODO: 将还有机会，但可能不满足到达时间约束的港口加入候选港口
                candidateRends.add(tRend.getName());
            }

        }

        logistics.setDestinations(candidateRends);
        //TODO: find best rendezvous
        Rendezvous bestRend = null;
        double minCost = infinite;
        int len = resultRends.size();
        logger.debug("---Select optimized solution based on the time urgency.---");
        if (len == 0) {
            logger.debug("No ports meeting the constrain.");
            logger.debug("Decision fail.");
            fail(routePlan);
        } else {
            for (int i = 0; i < len; i++) {
                Rendezvous tRend = resultRends.get(i);
                double sumCost = tRend.getSumCost();
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
                //TODO: update logistics
                logger.info("---update logistics---");
                logistics.setRendezous(bestRend.getName());
                logistics.setTimeStamp(DateUtil.date2str(DateUtil.transForDate(currentMs)));
                restClient.putLogistic(logisticsPart, lpi.getId(), logistics);
                logger.debug("send rendezvous port to vessel : " + bestRend.getName());
                restClient.putRendezvous(vesselPart, vpi.getId(), bestRend.getName());
            } else {
                logger.debug("---Best Rendezvous Not Found---");
                missing(routePlan);
            }
        }
        return routePlan;
    }


    private void missing(RoutePlan routePlan) {
        Rendezvous tRen = new Rendezvous();
        tRen.setName("MISSING");
        routePlan.setRendezvous(tRen);
        routePlan.setRendezvousList(new ArrayList<Rendezvous>());
    }
    private void fail(RoutePlan routePlan) {
        Rendezvous tRen = new Rendezvous();
        tRen.setName("FAIL");
        routePlan.setRendezvous(tRen);
        routePlan.setRendezvousList(new ArrayList<Rendezvous>());
    }

}
