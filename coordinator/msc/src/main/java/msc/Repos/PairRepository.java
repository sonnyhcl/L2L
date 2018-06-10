package msc.Repos;

import lombok.Data;
import msc.domain.SupplierPart;
import msc.domain.SupplierProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import msc.domain.Pair;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class PairRepository {
    private final Logger logger = LoggerFactory.getLogger(PairRepository.class);

    private List<Pair> pairs = new ArrayList<Pair>();


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

    public void match(String orgId , String pid , SupplierProcessInstance spi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getSpi() == null){
                p.setSpi(spi);
            }
        }
    }



}
