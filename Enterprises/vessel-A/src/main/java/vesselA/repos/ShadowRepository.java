package vesselA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vesselA.domain.VesselShadow;
import vesselA.eventGateway.ShadowWithProcesses;

import java.util.ArrayList;
import java.util.List;


@Data
@Service
public class ShadowRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    List<ShadowWithProcesses> shadowProcessRegistry = new ArrayList<ShadowWithProcesses>();
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

    public ShadowWithProcesses findRegisteredProcessesById(String vid){
        for(ShadowWithProcesses spr : shadowProcessRegistry){
            if(spr.getVid().equals(vid)){
                return spr;
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

    public  void  saveRegistry(String vid , String pid){
        ShadowWithProcesses spr = findRegisteredProcessesById(vid);
        if(spr == null){
            ShadowWithProcesses e = new ShadowWithProcesses();
            e.setVid(vid);
            e.save(pid);
            shadowProcessRegistry.add(e);
        }else{
            spr.save(pid);
        }
    }


}
