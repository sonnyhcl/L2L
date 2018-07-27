package logisticsA.repos;

import logisticsA.domain.Logistics;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class LogisticsRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Logistics> logisticses= new ArrayList<Logistics>();

    public void save(Logistics logistics){
        logisticses.add(logistics);
    }

    public Logistics update(Logistics logistics){
        Logistics logistics1 = findById(logistics.getId());
        logistics1 = logistics;
        return logistics1;
    }
    public Logistics findById(String logisticId){
        for(Logistics logistics : logisticses){
            if(logisticId.equals(logistics.getId())){
                return logistics;
            }
        }
        return null;
    }

    public Logistics findByLpid(String lpid){
        for(Logistics logistics : logisticses){
            if(lpid.equals(logistics.getLpid())){
                return logistics;
            }
        }
        return null;
    }
}
