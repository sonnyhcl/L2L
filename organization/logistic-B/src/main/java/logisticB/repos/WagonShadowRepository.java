package logisticB.repos;

import logisticB.domain.WagonShadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
}
