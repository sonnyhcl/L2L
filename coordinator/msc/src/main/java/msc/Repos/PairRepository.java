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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Pair> pairs = new ArrayList<Pair>();


    public boolean isRegistried(String orgId , String pid){
        boolean isRegistried = false;

        for(Pair pair : pairs){

            isRegistried = pair.isRegistried(orgId,pid);
            if(isRegistried == true){
                return true;
            }

        }
        return isRegistried;
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

    public void match(String orgId , String pid , SupplierProcessInstance spi){
        for(Pair p : pairs){
            p.isRegistried(orgId , pid);
            if(p.getSpi() == null){
                p.setSpi(spi);
            }
        }
    }



}
