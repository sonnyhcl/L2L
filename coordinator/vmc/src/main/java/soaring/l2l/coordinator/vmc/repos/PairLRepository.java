package soaring.l2l.coordinator.vmc.repos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import soaring.l2l.coordinator.vmc.domain.ManagerPart;
import soaring.l2l.coordinator.vmc.domain.Pair;

import java.util.List;
@Data
@NoArgsConstructor
@Service
public class PairLRepository {
    private final Logger logger = LoggerFactory.getLogger(PairLRepository.class);

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

    public void match(String orgId , String pid , ManagerPart managerPart){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getManagerPart() == null){
                p.setManagerPart(managerPart);
            }
        }
    }
}
