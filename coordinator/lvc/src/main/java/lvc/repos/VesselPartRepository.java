package lvc.repos;

import lombok.Data;
import lvc.domain.VesselPart;
import lvc.util.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class VesselPartRepository {
    private static  final Logger logger = LoggerFactory.getLogger(VesselPartRepository.class);
    private List<VesselPart>  vesselParts = new ArrayList<VesselPart>();

    public VesselPartRepository(@Value("${vesselParts.fileName}") String fileName) throws IOException {
        //register vessel organization
        logger.info("--"+fileName+"--");
        String filePath = this.getClass().getResource("/").getPath()+"data/"+fileName;
        vesselParts = CsvUtil.readVesselParts(filePath);
    }
    public  void  save(VesselPart vesselPart){
        if(findByOrgId(vesselPart.getOrgId()) == null){
            vesselParts.add(vesselPart);
        }else{
            logger.debug("This enterprise has been registered.");
        }
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
