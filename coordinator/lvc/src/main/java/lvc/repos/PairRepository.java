package lvc.repos;

import lombok.Data;
import lvc.domain.LogisticProcessInstance;
import lvc.domain.Pair;
import lvc.domain.VesselProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class PairRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Pair> pairs = new ArrayList<Pair>();

    public boolean isRegistried(String orgId , String pid){
        boolean isReg = false;

        for(Pair pair : pairs){

            isReg = pair.isRegistried(orgId,pid);
            if(isReg == true){
                return true;
            }


        }
        return isReg;
    }

    public boolean isPaired(String orgId , String pid){
        boolean paired = false;

        for(Pair pair : pairs){
            paired = pair.isPaired(orgId , pid);
            if(paired == true){
                return true;
            }
        }

        return paired;
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

    public  Pair findById(String orgId , String pid){
        for(Pair pair : pairs){
            if(pair.isPaired(orgId , pid)){
                return pair;
            }
        }
        return null;
    }

    public void createPair(VesselProcessInstance vpi , LogisticProcessInstance lpi){
        pairs.add(new Pair(vpi , lpi));
    }


}
