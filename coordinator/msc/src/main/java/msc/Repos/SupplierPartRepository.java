package msc.Repos;

import lombok.Data;
import msc.domain.SupplierPart;
import msc.util.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class SupplierPartRepository {
    private static  final Logger logger = LoggerFactory.getLogger(SupplierPartRepository.class);
    private List<SupplierPart> supplierParts = new ArrayList<SupplierPart>();
    public SupplierPartRepository(@Value("${supplierParts.fileName}") String fileName) throws IOException {
        //register vessel organization
        logger.info("--"+fileName+"--");
        String filePath = this.getClass().getResource("/").getPath()+"data/"+fileName;
        supplierParts = CsvUtil.readSupplierParts(filePath);
    }
    public  void  save(SupplierPart supplierPart){
        if(findByOrgId(supplierPart.getOrgId()) == null){
            supplierParts.add(supplierPart);
        }else{
            logger.debug("This enterprise has been registered.");
        }
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
