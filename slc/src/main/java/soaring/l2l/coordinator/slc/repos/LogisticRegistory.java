package soaring.l2l.coordinator.slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import soaring.l2l.coordinator.slc.domain.LogisticPart;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class LogisticRegistory {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<LogisticPart> logisticParts;

    public LogisticRegistory(){
        logisticParts = new ArrayList<LogisticPart>();
        logisticParts.add(new LogisticPart("顺丰速运", "L1001" , null , null , "fixed-rendezvous"));
        logisticParts.add(new LogisticPart("圆通速运", "L1003" , null , null , "variable-rendezvous"));
    }

    public LogisticPart findByLOrgId(String orgId){
        for(LogisticPart lp : logisticParts){
            if(lp.getOrgId().equals(orgId)){
                return lp;
            }
        }
        return null;
    }

}
