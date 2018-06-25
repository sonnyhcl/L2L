package vmc.repos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vmc.domain.ManagerPart;
import vmc.domain.ManagerProcessInstance;
import vmc.domain.Pair;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@Service
public class PairRepository {
    private final Logger logger = LoggerFactory.getLogger(PairRepository.class);

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
        boolean isPaired = false;

        for(Pair pair : pairs){
           isPaired = pair.isPaired(orgId , pid);
           if(isPaired == true){
               return true;
           }
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
