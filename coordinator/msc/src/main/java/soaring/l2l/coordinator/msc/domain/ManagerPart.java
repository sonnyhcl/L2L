package soaring.l2l.coordinator.msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerPart extends Participant {
    public ManagerPart(String orgId , String pid){
        this.orgId = orgId;
        this.pid = pid;
    }
    public static String getUrl(){
        return getUrl()+":8082/manager-app";
    }

    @Override
    public String toString() {
        return "ManagerPart{" +
                "orgId='" + orgId + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
