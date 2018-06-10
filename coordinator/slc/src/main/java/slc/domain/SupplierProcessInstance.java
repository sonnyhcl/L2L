package slc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("all")
@NoArgsConstructor
public class SupplierProcessInstance extends ProcessInstance{
    private String orderId;
    private String logisticId;

    public SupplierProcessInstance(String id, String orgId, String orderId, String logisticId) {
        super(id, orgId);
        this.orderId = orderId;
        this.logisticId = logisticId;
    }

    public SupplierProcessInstance(String id, String orgId) {
        super(id, orgId);
    }
}
