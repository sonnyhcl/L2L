package lvc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("all")
@NoArgsConstructor
public class LogisticsProcessInstance extends ProcessInstance{
    private String logisticId;
    private String wid;

    public LogisticsProcessInstance(String id, String orgId, String logisticId, String wid) {
        super(id, orgId);
        this.logisticId = logisticId;
        this.wid = wid;
    }

    public LogisticsProcessInstance(String id, String orgId) {
        super(id, orgId);
    }
}
