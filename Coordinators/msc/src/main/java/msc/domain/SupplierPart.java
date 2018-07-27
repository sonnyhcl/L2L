package msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierPart extends Participant {
    private String category;
    private String location;

    public SupplierPart(String orgName, String orgId, String host, String port, String projectId, String category, String location) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
        this.location = location;
    }

    public SupplierPart deepCopy(){
        SupplierPart supplierPart = new SupplierPart(orgName, orgId, host, port, projectId,category, location);
        return supplierPart;
    }
}
