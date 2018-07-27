package vesselA.domain;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class VesselShadow {
    private String id; // vid
    private double longitude;
    private double latitude;
    private double velocity;
    private  String timeStamp;
    private List<Destination> destinations;
    private String status;
    private String startTime;
    private int stepIndex;


    public List<Destination> getRemainingDestinations(){
        List<Destination> dlist = new ArrayList<Destination>();
        for(int i = 0 ; i < destinations.size(); i++){
            if(i >= stepIndex){
                Destination d = destinations.get(i);
//                if(i == stepIndex) {
//                    if ("Anchoring".equals(status) || "Docking".equals(status)) {
//                        dlist.add(destinations.get(stepIndex));
//                    }
//                }
                dlist.add(d);
            }
        }

        return dlist;
    }

    public void updateState(double longitude , double latitude , double velocity , String timeStamp ){
        this.longitude = longitude;
        this.latitude =latitude;
        this.velocity = velocity;
        this.timeStamp = timeStamp;
    }


}
