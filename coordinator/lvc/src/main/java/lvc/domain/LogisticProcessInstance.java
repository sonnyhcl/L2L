package lvc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("all")
@NoArgsConstructor
public class LogisticProcessInstance extends ProcessInstance{
    private String logisticId;
    private String wid;

    public LogisticProcessInstance(String id, String orgId, String logisticId, String wid) {
        super(id, orgId);
        this.logisticId = logisticId;
        this.wid = wid;
    }

    public LogisticProcessInstance(String id, String orgId) {
        super(id, orgId);
    }
}
