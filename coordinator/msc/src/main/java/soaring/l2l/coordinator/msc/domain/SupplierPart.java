package soaring.l2l.coordinator.msc.domain;

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


    public SupplierPart(String orgName, String orgId, String pid, String url, double longitude, double latitude) {
        super(orgName, orgId, pid, url);
        this.longitude = longitude;
        this.latitude = latitude;
    }



    public SupplierPart deepCopy(){
        SupplierPart supplierPart = new SupplierPart(orgName ,orgId,  pid,  url , longitude,  latitude);
        return supplierPart;
    }
}
