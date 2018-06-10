package msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierPart extends Participant {
    private String category;
    private double longitude;
    private double latitude;

    public SupplierPart(String orgName, String orgId, String host, String port, String projectId, String category, double longitude, double latitude) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public SupplierPart deepCopy(){
        SupplierPart supplierPart = new SupplierPart(orgName, orgId, host, port, projectId,category, longitude, latitude);
        return supplierPart;
    }
}
