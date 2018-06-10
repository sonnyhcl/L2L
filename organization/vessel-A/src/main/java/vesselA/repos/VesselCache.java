package vesselA.repos;

import lombok.Data;
import org.springframework.stereotype.Component;
import vesselA.vessel.domain.VesselShadow;

import java.util.Map;

/**
 * vessel data cache for awsiot shadow and related process data by vId
 * @author bqzhu
 */
@Component("vesselCache")
@Data
public class VesselCache {
    //船的Iot数据, vId-->vesselShadow
    private  Map<String , VesselShadow> vesselShadows;

    private long zoomVal;

    private final String coBasePath = "http://10.131.245.91:8091/vmc";
}
