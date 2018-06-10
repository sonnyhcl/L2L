package lvc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogisticPart extends Participant {
    private String category;

    public LogisticPart(String orgName, String orgId, String host, String port, String projectId, String category) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
    }
}
