package soaring.l2l.coordinator.vmc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VesselPart extends Participant {
    private String vid; //Physic object id , if there is a physic object corresponding process instance
    public VesselPart(String orgId , String pid , String vid){
        this.orgId = orgId;
        this.pid = pid;
        this.vid = vid;
    }
    public static String getUrl(){
        return getUrl()+":8081/vessel-app";
    }

}
