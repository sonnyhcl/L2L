package lvc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class Application {
    private String id;
    private String vOrgId;
    private String vpid;
    private String vid;
    private String mOrgId;
    private String mpid;
    private String spName;
    private int spNumber;
    private String rendezvous;
    private List<String> destinations;
    private String status;
    private String timeStamp;

    public Application(String id, String vOrgId, String vpid, String vid, String mOrgId, String mpid, String spName, int spNumber, List<String> destinations, String timeStamp) {
        this.id = id;
        this.vOrgId = vOrgId;
        this.vpid = vpid;
        this.vid = vid;
        this.mOrgId = mOrgId;
        this.mpid = mpid;
        this.spName = spName;
        this.spNumber = spNumber;
        this.destinations = destinations;
        this.timeStamp = timeStamp;
    }
}
