package soaring.l2l.coordinator.msc.Repos;

import lombok.Data;
import org.springframework.stereotype.Service;
import soaring.l2l.coordinator.msc.domain.SupplierPart;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class SupplierRepository {
    private List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();

    public SupplierRepository(){
        supplierParts.add(new SupplierPart("深圳备件供应商","S1001" , null , "http://10.131.245.91:8091/supplier-app",  114.067102,22.542207));
        supplierParts.add(new SupplierPart("合肥备件供应商","S1002" , null , "http://10.131.245.91:8092/supplier-app", 117.31024,31.883701));
        supplierParts.add(new SupplierPart("南京备件供应商","S1003" , null , "http://10.131.245.91:8093.3/supplier-app", 118.725052,32.007212));
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
