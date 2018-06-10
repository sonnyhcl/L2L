package lvc.repos;

import lombok.Data;
import lvc.domain.LogisticPart;
import lvc.domain.Pair;
import lvc.domain.VesselProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Data
@Service
public class PairRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Pair> pairs;

    public boolean isRegistried(String orgId , String pid){
        boolean isRegistried = false;

        for(Pair pair : pairs){

            pair.isRegistried(orgId,pid);

        }
        return isRegistried;
    }

    public boolean isPaired(String orgId , String pid){
        boolean isPaired = false;

        for(Pair pair : pairs){
           pair.isPaired(orgId , pid);
        }

        return isPaired;
    }

    public void register(Pair pair){
        pairs.add(pair);
    }

    public void match(String orgId , String pid , VesselProcessInstance vpi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getVpi() == null){
                p.setVpi(vpi);
            }
        }
    }


}
