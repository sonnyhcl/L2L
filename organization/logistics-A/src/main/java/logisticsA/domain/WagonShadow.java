package logisticsA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WagonShadow {
    private String id;
    private double longitude;
    private double latitude;
    private double speed;
    private double movedDistance;

    private String wpid;
    private String status;
    private Rendezvous rendezvous;
    private double lastNavsCost;
    private double deltaNavCost;
    private double deltaNavDist;

    public WagonShadow(String id , double longitude , double latitude , String status){
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
    }
}
