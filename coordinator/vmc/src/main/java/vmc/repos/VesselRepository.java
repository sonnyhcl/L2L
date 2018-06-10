package vmc.repos;

import lombok.Data;
import org.springframework.stereotype.Service;
import vmc.domain.Participant;
import vmc.domain.VesselPart;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class VesselRepository {
    private List<VesselPart>  vesselParts = new ArrayList<VesselPart>();

    public VesselRepository(){
        //register vessel organization
        vesselParts.add(new VesselPart("散货运输公司" , "VA1001" , "10.131.245.91" , "9001" , "vessel-A" , null));
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
