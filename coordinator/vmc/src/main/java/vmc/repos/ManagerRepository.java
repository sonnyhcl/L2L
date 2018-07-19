package vmc.repos;

import lombok.Data;
import org.springframework.stereotype.Service;
import vmc.domain.ManagerPart;
import vmc.domain.VesselPart;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class ManagerRepository {
    private List<ManagerPart> managerParts = new ArrayList<ManagerPart>();

    private ManagerRepository(){
        managerParts.add(new ManagerPart("船运管理公司","MA1001"  , "10.131.245.96","9011" , "manager-A" ,null));
    }

    public ManagerPart findByOrgId(String orgId){
        for(ManagerPart p : managerParts){
            if(orgId.equals(p.getOrgId())){
                return p;
            }
        }

        return null;
    }
}
