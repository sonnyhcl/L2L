package slc.repos;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import slc.domain.SupplierPart;

import java.util.ArrayList;
import java.util.List;


@Data
@Service
public class SupplierRepository {
    private static final Logger logger = LoggerFactory.getLogger(SupplierRepository.class);

    private List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();

    public SupplierRepository(){
        supplierParts.add(new SupplierPart("深圳备件供应商","SA1001"  , "10.131.245.96","9021" , "supplier-A" ,"A",  "深圳"));
        supplierParts.add(new SupplierPart("合肥备件供应商","SB1002"  , "10.131.245.96","9022" , "supplier-B" ,"B","合肥"));
        supplierParts.add(new SupplierPart("南京备件供应商","SC1003"  , "10.131.245.96" , "9023" ,"supplier-C", "C","南京"));
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
