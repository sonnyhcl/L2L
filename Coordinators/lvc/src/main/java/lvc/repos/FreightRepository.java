package lvc.repos;

import lombok.Data;
import lvc.domain.Freight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class FreightRepository {
    private  static final Logger logger = LoggerFactory.getLogger(FreightRepository.class);

    private List<Freight> freights = new ArrayList<Freight>();

    public void save(Freight freight){
        freights.add(freight);
    }

    public double findByName(String name){
        for(Freight freight : freights){
            if(name.equals(freight.getName())){
                return freight.getFreightRate();
            }
        }
        return 0.0;
    }
}
