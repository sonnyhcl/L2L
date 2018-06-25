package vesseldevA.repos;

import jxl.read.biff.BiffException;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vesseldevA.domain.Destination;
import vesseldevA.util.ExcelUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class DestinationRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Destination> destinations = new ArrayList<Destination>();

    public DestinationRepository(@Value("${vessel.destinations.fileName}") String destinationsXls ,
                              @Value("${vessel.destinations.sheetName}") String sheetName) throws IOException, BiffException {
        logger.debug(destinationsXls+"--"+sheetName);
        String path = this.getClass().getResource("/").getPath()+destinationsXls;
        destinations = ExcelUtil.readDestinations(path , sheetName);
        logger.debug("destinations : "+destinations.toString());
    }

    public Destination findDestination(String name){
        for(Destination destination : destinations){
            if(name.equals(destination.getName())){
                return destination;
            }
        }
        return null;
    }
}
