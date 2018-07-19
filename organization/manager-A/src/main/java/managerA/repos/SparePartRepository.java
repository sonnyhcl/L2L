package managerA.repos;

import lombok.Data;
import managerA.domain.SparePart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@Service
public class SparePartRepository {
    private static final Logger logger = LoggerFactory.getLogger(SparePartRepository.class);
    private List<SparePart> spareParts = new ArrayList<SparePart>();

    public SparePartRepository(){
        logger.info("--SparePartRepository--");
        //TODO : read from xxx.xls
        spareParts.add(new SparePart("缸盖" , "A" , 3.9));
        spareParts.add(new SparePart("螺丝" , "B" , 3.7));
        spareParts.add(new SparePart("钢筋" , "C" , 5.8));


    }

    public void save(SparePart sparePart){
        spareParts.add(sparePart);
    }

    public SparePart findByName(String spName){
        for(SparePart sp : spareParts){
            if(spName.equals(sp.getName())){
                return sp;
            }
        }
        return null;
    }

}
