package lvc.repos;

import lombok.Data;
import lvc.domain.LogisticPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class LogisticRepository {
    private static final Logger logger = LoggerFactory.getLogger(LogisticRepository.class);

    private List<LogisticPart> logisticParts;

    public LogisticRepository() throws IOException {
        logger.info("--LogisticRepository--");
        logisticParts = new ArrayList<LogisticPart>();
        logisticParts.add(new LogisticPart("深圳顺丰速运", "LA1001" , "10.131.245.96","9031" , "logistic-A" ,"variable-rendezvous" ));
        logisticParts.add(new LogisticPart("南京圆通速运", "LB1002" , "10.131.245.96","9032" , "logistic-B" , "fixed-rendezvous"));
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
