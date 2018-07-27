package msc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
public class Crane {
    private String name;
    private boolean craneStart;
    private double weightLimit;
}
