package vesseldevA.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("all")
public class VesselState {
    private double longitude;
    private double latitude;
    private double velocity;
    private  String timeStamp;

    public VesselState(double longitude, double latitude, double velocity, String timeStamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.velocity = velocity;
        this.timeStamp = timeStamp;
    }

    public VesselState deepCopy(){
        VesselState res = new VesselState();
        res.setTimeStamp(this.timeStamp);
        res.setLatitude(this.latitude);
        res.setLongitude(this.longitude);
        res.setVelocity(this.velocity);
        return res;
    }
}
