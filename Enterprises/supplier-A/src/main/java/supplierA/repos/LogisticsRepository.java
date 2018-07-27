package supplierA.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import supplierA.domain.Logistics;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class LogisticsRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Logistics> logisticses = new ArrayList<Logistics>();

    public void save(Logistics logistics){
        logisticses.add(logistics);
    }

    public Logistics findById(String logisticId){
        for(Logistics logistics : logisticses){
            if(logisticId.equals(logistics.getId())){
                return logistics;
            }
        }
        return null;
    }
}
