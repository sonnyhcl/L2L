package supplierA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
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

    public Logistic generateLogistic(String logisticId, String lOrgId, String lpid, String wid , String category,
                                     String rendezvous, String supLoc) {
        Logistic logistic = new Logistic(logisticId, this.vOrgId, vpid, vid, mOrgId, mpid, sOrgId,
                spid, lOrgId, lpid , wid , spName, spWight, spNumber, category, rendezvous, supLoc, destinations, timeStamp);

        return logistic;

    }
}
