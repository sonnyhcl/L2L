package vesseldevA.repos;

import jxl.read.biff.BiffException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vesseldevA.domain.Location;
import vesseldevA.util.ExcelUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Data
@Service
public class LocationRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Location> locations = new ArrayList<Location>();

    public LocationRepository(@Value("${vessel.locations.fileName}") String locationsXls ,
                              @Value("${vessel.locations.sheetName}") String sheetName) throws IOException, BiffException {
        logger.debug(locationsXls+"--"+sheetName);
        String path = this.getClass().getResource("/").getPath()+locationsXls;
        locations = ExcelUtil.readPorts(path , sheetName);
        logger.debug("locations : "+locations.toString());

    }

    public Location findLocation(String name){
        for(Location location : locations){
            if(name.equals(location.getName())){
                return location;
            }
        }
        return null;
    }
}
