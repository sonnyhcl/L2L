package vesselA.domain;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class VesselShadow {
    //vessel shadow data
    private String id; // vid
    private double longitude;
    private double latitude;
    private double velocity;
    private  String timeStamp;
    private List<Destination> destinations;
    private  int positionIndex = 0;

    //process data
    private String vpid;
    private String status;
    private String simuStartTime;
    private int nextPortIndex;
    private int zoomInVal;
    private int defaultDelayHour;

    public List<Destination> getRemainingDestinations(){
        List<Destination> dlist = new ArrayList<Destination>();
        for(int i = 0 ; i < destinations.size(); i++){
            if(i >= nextPortIndex){
                Destination d = destinations.get(i);
                if(i == nextPortIndex) {
                    if ("Anchoring".equals(status) || "Docking".equals(status)) {
                        dlist.add(destinations.get(nextPortIndex - 1));
                    }
                }
                dlist.add(d);
            }
        }

        return dlist;
    }

    public void updateVesselState(VesselState vesselState){
        this.longitude = vesselState.getLongitude();
        this.latitude =vesselState.getLatitude();
        this.velocity = vesselState.getVelocity();
        this.timeStamp = vesselState.getTimeStamp();
    }


}
