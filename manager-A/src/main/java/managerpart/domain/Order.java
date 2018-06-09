package managerpart.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Order {
    private String id;
    private String vOrgId;
    private String vpid;
    private String vid;
    private String mOrgId;
    private String mpid;
    private String sOrgId;
    private String spid;
    private String spName;
    private String spNumber;
    private List<String> candidatePorts;
    private String timeStamp;

    public Order(String id, String vOrgId, String vpid, String vid, String mOrgId, String mpid,
                 String sOrgId, String spid, String spName, String spNumber, List<String> candidatePorts, String timeStamp) {
        this.id = id;
        this.vOrgId = vOrgId;
        this.vpid = vpid;
        this.vid = vid;
        this.mOrgId = mOrgId;
        this.mpid = mpid;
        this.sOrgId = sOrgId;
        this.spid = spid;
        this.spName = spName;
        this.spNumber = spNumber;
        this.candidatePorts = candidatePorts;
        this.timeStamp = timeStamp;
    }
}
