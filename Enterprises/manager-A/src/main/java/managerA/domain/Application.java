package managerA.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
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
    private List<String> destinations;
    private String timeStamp;


    public Order generateOrder(String orderId ,  String sOrgId , String spid){
        Order order = new Order(orderId, this.vOrgId, this.vpid, this.vid,  this.mOrgId,  this.mpid,
                 sOrgId,  spid,  this.spName,  this.spNumber, this.destinations,  this.timeStamp);
        return order;
    }
}
