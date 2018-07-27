package vesselA.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
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

    public Application(String id, String vOrgId, String vpid, String vid, String spName, int spNumber, List<String> destinations, String timeStamp) {
        this.id = id;
        this.vOrgId = vOrgId;
        this.vpid = vpid;
        this.vid = vid;
        this.spName = spName;
        this.spNumber = spNumber;
        this.destinations = destinations;
        this.timeStamp = timeStamp;
    }
}
