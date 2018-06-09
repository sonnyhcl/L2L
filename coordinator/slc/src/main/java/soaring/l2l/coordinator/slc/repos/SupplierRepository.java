package soaring.l2l.coordinator.slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import soaring.l2l.coordinator.slc.domain.SupplierPart;

import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class SupplierRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<SupplierPart> supplierParts;

    public SupplierRepository(){

            supplierParts = new ArrayList<SupplierPart>();
            supplierParts.add(new SupplierPart("深圳备件供应商","S1001" , null , 114.067102,22.542207));
            supplierParts.add(new SupplierPart("合肥备件供应商","S1002" , null , 117.31024,31.883701));
            supplierParts.add(new SupplierPart("南京备件供应商","S1003" , null , 118.725052,32.007212));
    }

    public SupplierPart findBySOrgId(String orgId){
        for(SupplierPart sp : supplierParts){
            if(sp.getOrgId().equals(orgId)){
                return sp;
            }
        }
        return null;
    }
}
