package logisticsA.repos;

import logisticsA.domain.WagonShadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class WagonShadowRepository {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private List<WagonShadow> wagonShadows = new ArrayList<WagonShadow>();


    public void save(WagonShadow ws){
        wagonShadows.add(ws);
    }

    public WagonShadow findById(String wid){
        for(WagonShadow ws : wagonShadows){
            if(wid.equals(ws.getId())){
                return ws;
            }
        }
        return null;
    }

    public WagonShadow findByPid(String pid){
        for(WagonShadow ws : wagonShadows){
            if(pid.equals(ws.getWpid())){
                return ws;
            }
        }
        return null;
    }
}
