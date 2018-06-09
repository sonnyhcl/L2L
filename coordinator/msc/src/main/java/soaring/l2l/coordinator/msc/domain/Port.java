package soaring.l2l.coordinator.msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
public class Port {
    private String pname;
    private double storageRate;
    private boolean craneStart;
    private double weightLimit;
}
