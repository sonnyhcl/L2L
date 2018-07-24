package logisticsA.repos;

import logisticsA.domain.Rendezvous;
import logisticsA.domain.RoutePlan;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Data
@Service
@SuppressWarnings("all")
public class RoutePlanRepository {
    private  static final Logger logger = LoggerFactory.getLogger(RoutePlanRepository.class);

    private static  int sequence = 0;

    private List<RoutePlan> routePlans = new ArrayList<RoutePlan>();

    public void save(RoutePlan routePlan){
        routePlan.setSequence(sequence++); // 标注第几次规划
        routePlans.add(routePlan);
    }

    public RoutePlan findById(String routePlanId){
        for(RoutePlan routePlan : routePlans){
            if(routePlanId.equals(routePlan.getId())){
                return routePlan;
            }
        }
        return null;
    }

    /**
     * 获取车流程实例的规划集,以规划的时间为顺序
     * @param lpid
     * @return
     */
    public List<RoutePlan> getRoutePlansByLpid(String lpid){
        List<RoutePlan> subRoutePlans = new  ArrayList<RoutePlan>();
//        logger.debug("routePlans : "+routePlans.toString());
        for(RoutePlan routePlan : routePlans){
            if(lpid.equals(routePlan.getLpid())){
                    subRoutePlans.add(routePlan);
            }
        }
        @SuppressWarnings("rawtypes")
        Comparator c = new Comparator<RoutePlan>() {
            @Override
            public int compare(RoutePlan a, RoutePlan b) {
                // TODO Auto-generated method stub
                if(a.getSequence() > b.getSequence()) {
                    return 1;
                }else {
                    return -1;
                }
            }
        };
        if(subRoutePlans.size()>1){
            subRoutePlans.sort(c);
            logger.debug("sub plan list : "+subRoutePlans.size());
        }
        return subRoutePlans;
    }

    /**
     * 变化前，前往上一次决策的目标港口的预计总成本
     * @param lpid
     * @return
     */
    public double getC0(String lpid){
        logger.debug("--getC0--");
        double C0 = 0.0;
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            if(size == 1){
                C0 = 0; // 代表不存在 ,  即是第一次规划
            }else{
                Rendezvous r = subRoutePlans.get(size-2).getRendezvous();
                if("FAIL".equals(r.getName())){//如果前一次规划失败
                    C0 = -1;//-1 代表无穷
                }else{
                    C0 = r.getSumCost();
                }
            }
            logger.debug("C0 = "+C0);

            return C0;
        }
        logger.error("No plan history!");
        return -2;
    }

    /**
     * 变化后，前往上一次决策的目标港口的预计总成本: C1 = C0 +　仓储费变化
     * 此处可以用不改变目的地方案的新成本替代
     * @param lpid
     * @return
     */
    public double getC1(String lpid){
        logger.debug("--getC1--");
        double C1 = 0.0;
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            if(size == 1){
                C1 = 0; // 代表不存在，即是第一次规划
            }else{
                Rendezvous r = subRoutePlans.get(size-2).getRendezvous();
                if("FAIL".equals(r.getName())){//如果前一次规划失败
                    C1 = -1;//-1 代表无穷
                }else{
                    Rendezvous r1 = getRendezvousByName(r.getName() , subRoutePlans.get(size-1).getRendezvousList());
                    C1 = r1 != null ? r1.getSumCost() : -1;
                }

            }
            logger.debug("C1 = "+C1);
            return C1;
        }
        logger.error("No plan history!");
        return -2; // error
    }

    /**
     * 决策后，前往新的目标港口的预计总成本
     * @param lpid
     * @return
     */
    public double getC2(String lpid){
        logger.debug("--getC2--");
        double C2 = 0.0;
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            Rendezvous r = subRoutePlans.get(size-1).getRendezvous();
            if("FAIL".equals(r.getName())){//如果当前规划失败
                C2 = -1;//-1 代表无穷 , Fail
            }else if("MISSING".equals(r.getName())){
                C2 = -3;//-3 代表无穷 , Missing
            }else{
                C2 = r.getSumCost();
            }
            logger.debug("C2 = "+C2);
            return C2;
        }
        logger.error("No plan history!");
        return -2; // error
    }

    public String getReason(String lpid){
        logger.debug("--getReason--");
        String reason = "NONE";
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            RoutePlan routePlan = subRoutePlans.get(size-1);
            HashMap<String , Object> msgBody = routePlan.getMsgBody();
            String eventType = msgBody.get("eventType").toString();
            logger.debug("event : "+eventType);
            switch (eventType){
                case "DELAY" :
                    String dy = msgBody.get("dy").toString();
                    Object dxObj = msgBody.get("dx");
                    String dx = "#";
                    if(dxObj != null){
                         dx = msgBody.get("dx").toString();
                    }
                    String phase = msgBody.get("phase").toString();
                    if("Docking".equals(phase)){
                        reason = "Docking : "+"postpone : "+ dy+"h.";
                    }else{
                        reason = phase + " : "+"delay : "+dx+"h , "+"postpone : "+ dy+"h.";
                    }
                    break;
                case "MISSING" :
                    reason = "Missing delivery opportunity.";
                    break;
                case "MEETING" :
                    reason = "Successful delivery of spare parts.";
                    break;
                case "TRAFFIC" :
                    reason = "Traffic jam ，delivery failed！";
                    break;
                case "INITIATING" :
                    reason = "Initial  planning";
                    break;
                default :
                    logger.debug("Not known reason");
                    break;
            }
            logger.debug(reason);
            return reason;
        }
        logger.error("No plan history!");
        return reason; //
    }


    public String getEventType(String lpid){
        logger.debug("--getEventType--");
        String eventType = "NONE";
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            RoutePlan routePlan = subRoutePlans.get(size-1);
            HashMap<String , Object> msgBody = routePlan.getMsgBody();
            eventType = msgBody.get("eventType").toString();
            return eventType;
        }

        logger.error("No plan history!");
        return eventType; //
    }

    public Rendezvous getRendezvousByName(String name  , List<Rendezvous> rendezvousList){
        for(Rendezvous r : rendezvousList){
                if(name.equals(r.getName())){
                    return  r;
                }
        }
        return null;
    }

    public  RoutePlan getLatestPlan(String lpid){
        List<RoutePlan> subRoutePlans = getRoutePlansByLpid(lpid);
        int size = subRoutePlans.size();
        if(size > 0){
            return subRoutePlans.get(size-1);
        }
        return null;
    }
}
