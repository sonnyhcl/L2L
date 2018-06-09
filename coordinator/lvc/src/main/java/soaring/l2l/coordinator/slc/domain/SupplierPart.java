package soaring.l2l.coordinator.slc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierPart extends Participant {
    private double longitude;
    private double latitude;
    public  static String getUrl(){
        return getUrl()+":8083/supplier-app";
    }

    public SupplierPart(String orgName , String orgId, String pid, double longitude, double latitude) {
        super(orgName , orgId, pid);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "SupplierPart{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgId='" + orgId + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }

    public SupplierPart deepCopy(){
        SupplierPart supplierPart = new SupplierPart(orgName ,orgId,  pid,  longitude,  latitude);
        return supplierPart;
    }
}
