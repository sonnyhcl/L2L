package managerA.domain;

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
    private double spWight;
    private int spNumber;
    private List<String> destinations;
    private String timeStamp;

    public Order(String id, String vOrgId, String vpid, String vid, String mOrgId, String mpid,
                 String sOrgId, String spid, String spName, int spNumber, List<String> destinations, String timeStamp) {
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
        this.destinations = destinations;
        this.timeStamp = timeStamp;
    }
}
