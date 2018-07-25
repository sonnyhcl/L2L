package slc.repos;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import slc.domain.LogisticsPart;
import slc.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class LogisticsPartRepository {
    private static final Logger logger = LoggerFactory.getLogger(LogisticsPartRepository.class);

    private List<LogisticsPart> logisticsParts;

    public LogisticsPartRepository(@Value("${logisticsParts.fileName}") String fileName) throws IOException {
        //register vessel organization
        logger.info("--"+fileName+"--");
        String filePath = this.getClass().getResource("/").getPath()+"data/"+fileName;
        logisticsParts = CsvUtil.readLogisticsParts(filePath);
    }
    public  void  save(LogisticsPart logisticsPart){
        if(findByLOrgId(logisticsPart.getOrgId()) == null){
            logisticsParts.add(logisticsPart);
        }else{
            logger.debug("This enterprise has been registered.");
        }
    }
    public LogisticsPart findByLOrgId(String orgId){
        for(LogisticsPart lp : logisticsParts){
            if(lp.getOrgId().equals(orgId)){
                return lp;
            }
        }
        return null;
    }

}
