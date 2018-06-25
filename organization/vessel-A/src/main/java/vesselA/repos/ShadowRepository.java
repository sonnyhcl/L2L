package vesselA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vesselA.domain.VesselShadow;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class ShadowRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    List<VesselShadow> vesselShadows = new ArrayList<VesselShadow>();

    public void save(VesselShadow vs){
        vesselShadows.add(vs);
    }

    public VesselShadow findById(String id){
        for(VesselShadow vesselShadow : vesselShadows){
            if(id.equals(vesselShadow.getId())){
                return vesselShadow;
            }
        }
        return null;
    }

    public VesselShadow findByPid(String pid){
        for(VesselShadow vesselShadow : vesselShadows){
            if(pid.equals(vesselShadow.getVpid())){
                return vesselShadow;
            }
        }
        return null;
    }
    public  VesselShadow update(VesselShadow newVs){
        String vid = newVs.getId();
        VesselShadow oldVs = findById(vid);
        oldVs = newVs;
        return findById(vid);
    }
}
