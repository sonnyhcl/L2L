package slc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogisticsPart extends Participant {
    private String category;

    public LogisticsPart(String orgName, String orgId, String host, String port, String projectId, String category) {
        super(orgName, orgId, host, port, projectId);
        this.category = category;
    }
}
