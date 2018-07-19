package msc.Repos;

import jxl.read.biff.BiffException;
import lombok.Data;
import msc.domain.Crane;
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
public class CraneRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<Crane> cranes = new ArrayList<Crane>();
    private String fileName;
    private String sheetName;

    public CraneRepository(@Value("${msc.cranes.file}") String fileName) throws IOException, BiffException {
        logger.info(fileName+"--"+sheetName);
        String path = getClass().getResource("/").getPath()+"/"+fileName;
        cranes = CsvUtil.readCranes(path);
        logger.debug(cranes.toString());
    }

    public double queryWeightLimit(String name){
        for(Crane crane : cranes){
            if(name.equals(crane.getName())){
                return crane.getWeightLimit();
            }
        }
        return 0.0;
    }
}
