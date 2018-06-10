package slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import slc.domain.LogisticPart;
import slc.domain.LogisticProcessInstance;
import slc.domain.Pair;

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

    public void match(String orgId , String pid , LogisticProcessInstance lpi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getLpi() == null){
                p.setLpi(lpi);
            }
        }
    }


}
