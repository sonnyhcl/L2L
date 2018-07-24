package slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import slc.domain.SupplierPart;
import slc.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class SupplierRepository {
    private static  final Logger logger = LoggerFactory.getLogger(SupplierRepository.class);
    private List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();
    public SupplierRepository(@Value("${supplierParts.fileName}") String fileName) throws IOException {
        //register vessel organization
        logger.info("--"+fileName+"--");
        String filePath = this.getClass().getResource("/").getPath()+"data/"+fileName;
        supplierParts = CsvUtil.readSupplierParts(filePath);
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
