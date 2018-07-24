package msc.Repos;

import lombok.Data;
import msc.domain.ManagerPart;
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
public class ManagerRepository {
    private static  final Logger logger = LoggerFactory.getLogger(ManagerRepository.class);
    private List<ManagerPart> managerParts = new ArrayList<ManagerPart>();

    public ManagerRepository(@Value("${managerParts.fileName}") String fileName) throws  IOException {
        //register vessel organization
        logger.info("--"+fileName+"--");
        String filePath = this.getClass().getResource("/").getPath()+"data/"+fileName;
        managerParts = CsvUtil.readManagerParts(filePath);
    }
    public  void  save(ManagerPart managerPart){
        if(findByOrgId(managerPart.getOrgId()) == null){
            managerParts.add(managerPart);
        }else{
            logger.debug("This enterprise has been registered.");
        }
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
