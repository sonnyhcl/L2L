package vesselA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public class VesselState {
    private double longitude;
    private double latitude;
    private double velocity;
    private  String timeStamp;
    public VesselState deepCopy(){
        VesselState res = new VesselState();
        res.setTimeStamp(this.timeStamp);
        res.setLatitude(this.latitude);
        res.setLongitude(this.longitude);
        res.setVelocity(this.velocity);
        return res;
    }
}
