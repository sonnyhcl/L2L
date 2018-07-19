package msc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("all")
//@NoArgsConstructor
public class ManagerProcessInstance extends ProcessInstance{
    private String applyId;
    private String orderId;

    public ManagerProcessInstance(String id, String orgId, String applyId, String orderId) {
        super(id, orgId);
        this.applyId = applyId;
        this.orderId = orderId;
    }

    public ManagerProcessInstance(String id, String orgId) {
        super(id, orgId);
    }
}
