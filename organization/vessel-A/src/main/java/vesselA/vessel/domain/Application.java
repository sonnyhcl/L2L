package vesselA.vessel.domain;

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
    private List<String> candidatePorts;
    private String timeStamp;

    public Application(String id, String vOrgId, String vpid, String vid, String spName, int spNumber, List<String> candidatePorts, String timeStamp) {
        this.id = id;
        this.vOrgId = vOrgId;
        this.vpid = vpid;
        this.vid = vid;
        this.spName = spName;
        this.spNumber = spNumber;
        this.candidatePorts = candidatePorts;
        this.timeStamp = timeStamp;
    }
}
