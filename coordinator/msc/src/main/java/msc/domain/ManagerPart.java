package msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class ManagerPart extends Participant {
    private String category;

    public ManagerPart(String orgName, String orgId, String host, String port, String projectId, String category) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
    }
}
