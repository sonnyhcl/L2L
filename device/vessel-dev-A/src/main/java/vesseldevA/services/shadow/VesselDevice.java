package vesseldevA.services.shadow;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.ToString;
import org.apache.log4j.Logger;
import vesseldevA.domain.Destination;
import vesseldevA.domain.VesselState;

import java.util.List;

@ToString
@JsonFilter("wantedProperties")
public class VesselDevice extends AWSIotDevice {
    private Logger logger = Logger.getLogger(this.getClass());

    public VesselDevice(String thingName) {
        super(thingName);
    }

    private String vid;

    private VesselState vesselState;

    private List<Destination> destinations;

    private int positionIndex;

    private String simuStartTime;

    private int nextPortIndex;

    private String status;

    public String getVid() {
        return vid;
    }

    public void updateVid(String vid) {
        this.vid = vid;
    }

    public VesselState getVesselState() {
        return vesselState;
    }

    public void updateVesselState(VesselState vesselState) {
        this.vesselState = vesselState;
    }



    public List<Destination> getDestinations() {
        return destinations;
    }


    public void updateDestinations(List<Destination> destinations){
        this.destinations = destinations;
    }
    public int getPositionIndex() {
        return positionIndex;
    }

    public void updatePositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public String getSimuStartTime() {
        return simuStartTime;
    }

    public void updateSimuStartTime(String simuStartTime) {
        this.simuStartTime = simuStartTime;
    }

    public int getNextPortIndex() {
        return nextPortIndex;
    }

    public void updateNextPortIndex(int nextPortIndex) {
        this.nextPortIndex = nextPortIndex;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
