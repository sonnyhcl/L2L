package soaring.l2l.coordinator.slc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogisticPart extends Participant {
    private String wid;
    private String category;

    public LogisticPart(String orgName, String orgId, String pid, String wid, String category) {
        super(orgName, orgId, pid);
        this.wid = wid;
        this.category = category;
    }

    public static String getUrl(){
        return getUrl()+":8084/logistic-app";
    }

    public LogisticPart deepCopy(){
        LogisticPart logisticPart = new LogisticPart(orgName, orgId, pid, wid, category);
        return logisticPart;
    }

    @Override
    public String toString() {
        return "LogisticPart{" +
                "wid='" + wid + '\'' +
                ", category='" + category + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgId='" + orgId + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
