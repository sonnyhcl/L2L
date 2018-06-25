package logisticA.repos;

import logisticA.domain.Location;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
@Data
public class LocationRepository {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Location> locations = new ArrayList<Location>();

    public void save(Location location){
        locations.add(location);
    }

    public Location findByName(String name){
        for(Location location : locations){
            if(name.equals(location.getName())){
                return location;
            }
        }
        return null;
    }
}
