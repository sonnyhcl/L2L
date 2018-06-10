package msc.Repos;

import lombok.Data;
import msc.domain.Participant;
import msc.domain.SupplierPart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class SupplierRepository {
    private List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();

    public SupplierRepository(){
        supplierParts.add(new SupplierPart("深圳备件供应商","SA1001"  , "10.131.245.91","9021" , "supplier-A" ,null,  114.067102,22.542207));
        supplierParts.add(new SupplierPart("合肥备件供应商","SB1002"  , "10.131.245.91","9022" , "supplier-B" ,null, 117.31024,31.883701));
        supplierParts.add(new SupplierPart("南京备件供应商","SC1003"  , "10.131.245.91" , "9023" ,"supplier-C", null,118.725052,32.007212));
    }

    public SupplierPart findByOrgId(String orgId){
        for(SupplierPart sp : supplierParts){
            if(sp.getOrgId().equals(orgId)){
                return sp;
            }
        }
        return null;
    }
}
