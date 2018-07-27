package lvc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@SuppressWarnings("all")
@NoArgsConstructor
@AllArgsConstructor
public class VesselProcessInstance extends ProcessInstance {
    private String vid;

    public VesselProcessInstance(String id, String orgId, String vid) {
        super(id, orgId);
        this.vid = vid;
    }
}
