package lvc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VesselPart extends Participant {
    private String category;

    public VesselPart(String orgName, String orgId, String host, String port, String projectId, String category) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
    }
}
