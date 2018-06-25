package lvc.repos;

import lombok.Data;
import lvc.domain.VesselPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@Service
public class VesselRepository {
    private static final Logger logger = LoggerFactory.getLogger(VesselRepository.class);
    private List<VesselPart> vesselParts = new ArrayList<VesselPart>();

    public VesselRepository(){
        //register vessel organization
        logger.info("--VesselRepository--");
        vesselParts.add(new VesselPart("散货运输公司" , "VA1001" , "10.131.245.91" , "9001" , "vessel-A" , "A"));
    }

    public VesselPart findByOrgId(String orgId){
        for(VesselPart p : vesselParts){
            if(orgId.equals(p.getOrgId())){
                return p;
            }
        }

        return null;
    }
}
