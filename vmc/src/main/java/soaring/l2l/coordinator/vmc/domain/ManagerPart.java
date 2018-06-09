package soaring.l2l.coordinator.vmc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
public class ManagerPart extends Participant {
    public ManagerPart(String orgId , String pid){
        this.orgId = orgId;
        this.pid = pid;
    }
    public  static String getUrl(){
        return getUrl()+":8082/manager-app";
    }
}
