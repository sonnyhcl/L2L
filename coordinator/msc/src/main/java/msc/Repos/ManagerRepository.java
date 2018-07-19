package msc.Repos;

import lombok.Data;
import msc.domain.ManagerPart;
import org.springframework.stereotype.Service;

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
