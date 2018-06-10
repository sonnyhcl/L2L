package vmc.repos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vmc.domain.ManagerPart;
import vmc.domain.ManagerProcessInstance;
import vmc.domain.Pair;

import java.util.List;
@Data
@NoArgsConstructor
@Service
public class PairRepository {
    private final Logger logger = LoggerFactory.getLogger(PairRepository.class);

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

    public void match(String orgId , String pid , ManagerProcessInstance mpi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getMpi() == null){
                p.setMpi(mpi);
            }
        }
    }
}
