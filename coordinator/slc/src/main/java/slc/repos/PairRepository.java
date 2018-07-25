package slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import slc.domain.LogisticProcessInstance;
import slc.domain.Pair;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class PairRepository {
    private static final Logger logger = LoggerFactory.getLogger(PairRepository.class);

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
            }        }

        return paired;
    }

    public void register(Pair pair){
        pairs.add(pair);
    }

    public void match(String orgId , String pid , LogisticProcessInstance lpi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getLpi() == null){
                p.setLpi(lpi);
            }
        }
    }


}
