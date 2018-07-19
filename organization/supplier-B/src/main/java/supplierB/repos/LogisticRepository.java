package supplierB.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import supplierA.domain.Logistic;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class LogisticRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Logistic> logistics = new ArrayList<Logistic>();

    public void save(Logistic logistic){
        logistics.add(logistic);
    }

    public Logistic findById(String logisticId){
        for(Logistic logistic : logistics){
            if(logisticId.equals(logistic.getId())){
                return logistic;
            }
        }
        return null;
    }
}
